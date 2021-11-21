package com.arui.mall.core.seckill.consumer;

import com.arui.mall.core.constant.MqConst;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.core.seckill.service.SeckillProductService;
import com.arui.mall.model.pojo.entity.PrepareSeckillOrder;
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
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
                redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT)
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

    /**
     * mq消费者，执行预下单操作
     * @param map
     * @param message
     * @param channel
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.PREPARE_SECKILL_QUEUE),
            exchange = @Exchange(value = MqConst.PREPARE_SECKILL_EXCHANGE),
            key = {MqConst.PREPARE_SECKILL_ROUTE_KEY}
    ))
    public void prepareConsumer(Map<String, String> map, Message message, Channel channel){
        String userId = map.get("userId");
        String skuId = map.get("skuId");
        SeckillProduct secKillProduct = (SeckillProduct) redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT).get(skuId);
        // 判断状态1
        if (RedisConstant.CAN_NOT_SECKILL.equals(secKillProduct.getStatus())) {
            // 没有商品了，返回信息
            return;
        }

        Boolean flag = redisTemplate.boundValueOps(RedisConstant.PREPARE_SECKILL_USERID_ORDER+userId).setIfAbsent(skuId);
        if (!flag){
            // 之前下过该订单
            return;
        }

        // 校验库存，如果有库存，需要 -1
        Integer hasStock = (Integer) redisTemplate.boundListOps(RedisConstant.SECKILL_STOCK_PREFIX + skuId).rightPop();
        if (hasStock == null){
            // 没有库存,通知其他节点更改状态位
            redisTemplate.convertAndSend(RedisConstant.PREPARE_PUB_SUB_SECKILL, skuId + ":" + RedisConstant.CAN_NOT_SECKILL);
            return;
        }
        //d.生成临时订单数据存储redis
        PrepareSeckillOrder prepareSeckillOrder = new PrepareSeckillOrder();
        prepareSeckillOrder.setUserId(userId);
        prepareSeckillOrder.setBuyNum(1);
        SeckillProduct seckillProduct = (SeckillProduct) redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT).get(String.valueOf(skuId));
        prepareSeckillOrder.setSeckillProduct(seckillProduct);

        // 设置订单码
        prepareSeckillOrder.setPrepareOrderCode(DigestUtils.md5DigestAsHex((userId + skuId).getBytes()));
        redisTemplate.boundHashOps(RedisConstant.PREPARE_SECKILL_USERID_ORDER).put(userId,prepareSeckillOrder);

        //更新库存量
        seckillProductService.updateSecKillStockCount(skuId);
    }

}
