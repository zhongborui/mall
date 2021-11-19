package com.arui.mall.core.seckill.consumer;

import com.arui.mall.core.constant.MqConst;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.core.seckill.service.SeckillProductService;
import com.arui.mall.model.pojo.entity.SeckillProduct;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @author ...
 */
@Component
public class SeckillConsumer {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private SeckillProductService seckillProductService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 通知上架接口，扫描数据库，添加商品到redis
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.SCAN_SECKILL_QUEUE),
            exchange = @Exchange(value = MqConst.SCAN_SECKILL_EXCHANGE),
            key = {MqConst.SCAN_SECKILL_ROUTE_KEY}
    ))
    public void scanDbToRedis(Message message, Channel channel){
        // 扫描数据库符合描述的商品，状态是审核通过的status为1，开始时间是今天，库存是>0
        LocalDate now = LocalDate.now();
        QueryWrapper<SeckillProduct> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "1")
                .eq("DATE_FORMAT(start_time,'%Y-%m-%d')", now)
                .gt("stock_count", 0);
        List<SeckillProduct> seckillProductList = seckillProductService.list(wrapper);
        System.out.println(seckillProductList);

        // 存入redis hash结构
        if (!CollectionUtils.isEmpty(seckillProductList)) {
            for (SeckillProduct seckillProduct : seckillProductList) {
                Long skuId = seckillProduct.getSkuId();
                redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT + skuId)
                        .put(String.valueOf(skuId), seckillProduct);

//                // 先判断缓存是否已经有该商品了
//                boolean flag = (boolean) redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT + skuId).hasKey(String.valueOf(skuId));
//                if (flag){
//                    // 说明有,跳出本次遍历
//                    continue;
//                }

                // 构建库存队列，防止超卖
                Integer stockNum = seckillProduct.getNum();
                for (Integer i = 0; i < stockNum; i++) {
                    redisTemplate.boundListOps(RedisConstant.SECKILL_STOCK_PREFIX + skuId)
                            .leftPush(skuId);
                }

                // redis发布订阅通知其他节点，改商品可以进行秒杀了
                redisTemplate.convertAndSend(RedisConstant.PREPARE_PUB_SUB_SECKILL, skuId + ":" + RedisConstant.CAN_SECKILL);
            }
        }

        // 手动确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }
}
