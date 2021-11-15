package com.arui.mall.core.order.service.impl;

import com.arui.mall.common.util.HttpClientUtil;
import com.arui.mall.core.constant.MqConst;
import com.arui.mall.core.order.config.MyThreadExecutor;
import com.arui.mall.core.order.mapper.OrderDetailMapper;
import com.arui.mall.core.order.mapper.OrderInfoMapper;
import com.arui.mall.core.order.service.OrderDetailService;
import com.arui.mall.core.order.service.OrderInfoService;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.enums.OrderStatus;
import com.arui.mall.model.enums.ProcessStatus;
import com.arui.mall.model.pojo.entity.OrderDetail;
import com.arui.mall.model.pojo.entity.OrderInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * 订单表 订单表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private OrderDetailService orderDetailService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${cancel.order.delay}")
    private Integer cancelOrderDelay;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    /**
     * 幂等性，防止无刷新重复提交表单
     * @param userId
     * @param tradeNo
     * @return
     */
    @Override
    public boolean checkTradNo(String userId, String tradeNo) {
        // 定义key
        String tradeNoKey = "user:"+userId+":tradeNo";
        String valueInRedis = (String) redisTemplate.opsForValue().get(tradeNoKey);
        return tradeNo.equals(valueInRedis);
    }

    /**
     * 校验价格和库存
     * @param orderInfo
     * @param userId
     * @return
     */
    @Override
    public List<String> checkPriceAndStock(OrderInfo orderInfo, String userId) {
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        List<String> warningList = new ArrayList<>();
        // 异步编排集合
        ArrayList<CompletableFuture<Void>> completableFutureArrayList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            Long skuId = orderDetail.getSkuId();

            CompletableFuture<Void> skuPriceCompletableFuture = CompletableFuture.runAsync(() -> {
                // 远程调用价格接口
                BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
                int i = skuPrice.compareTo(orderDetail.getOrderPrice());
                if (i != 0) {
                    // 说明价格不相等,需要修改价格并提示
                    warningList.add(orderDetail.getSkuName() + "价格有变化");
                }
            }, MyThreadExecutor.getInstance());
            completableFutureArrayList.add(skuPriceCompletableFuture);

            CompletableFuture<Void> hasStorageCompletableFuture = CompletableFuture.runAsync(() -> {
                // 远程调用库存接口 http://localhost:8100/hasStock?skuId=28&num=1000
                String hasStorage = HttpClientUtil.doGet("http://localhost:8100/hasStock?skuId=" + skuId + "&num=" + orderDetail.getSkuNum());
                if (!hasStorage.equals("1")) {
                    // 没有库存了
                    warningList.add(orderDetail.getSkuName() + "库存不足");
                }
            }, MyThreadExecutor.getInstance());
            completableFutureArrayList.add(hasStorageCompletableFuture);
        }
        // 先搞一CompletableFuture类型数组
        CompletableFuture[] completableFutureArray = new CompletableFuture[completableFutureArrayList.size()];
        CompletableFuture.allOf(completableFutureArrayList.toArray(completableFutureArray)).join();
        return warningList;
    }

    /**
     * 以上没有问题，删除流水号
     * @param userId
     */
    @Override
    public void deleteTradNo(String userId) {
        // 定义key
        String tradeNoKey = "user:"+userId+":tradeNo";
        redisTemplate.delete(tradeNoKey);
    }

    /**
     * 保存订单到数据库
     * @param orderInfo
     * @return
     */
    @Override
    public Long saveOrderDetail(OrderInfo orderInfo, Long userId) {
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();

        //设置一些页面没有传递过来的值
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        orderInfo.setUserId(userId);
        String outTradeNo = "ARUI" + System.currentTimeMillis() + "" + new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setTradeBody("购物商品信息");
        orderInfo.setCreateTime(new Date());
        //设置过期时间，默认一天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        orderInfo.setExpireTime(calendar.getTime());
        // 订单的进程状态 ，枚举类里包含了订单的状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        //设置订单总价，这里默认0.01
        orderInfo.setTotalMoney(new BigDecimal(0.01));
        baseMapper.insert(orderInfo);


        // 保存orderDetail表
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
        }
        orderDetailService.saveBatch(orderDetailList);

        //  订单编号因该放在消息队列里面， 订单过期自动取消，mq的延迟队列
        rabbitTemplate.convertAndSend(MqConst.CANCEL_ORDER_EXCHANGE,
                MqConst.CANCEL_ORDER_ROUTE_KEY,
                orderInfo.getId(),
                correlationData -> {
            // 设置延迟时间10s
                    correlationData.getMessageProperties().setDelay(cancelOrderDelay);
                    return correlationData;
                });

        // 返回订单编号
        return orderInfo.getId();
    }

    /**
     * 根据订单号查看订单的状态，如果还是未支付，则将订单关闭
     * @param id
     * @return
     */
    @Override
    public OrderInfo checkOrderStatus(Long id) {

        OrderInfo orderInfo = baseMapper.selectById(id);
        return orderInfo;
    }

    /**
     * 关闭订单
     * @param id
     * @param processStatus
     */
    @Override
    public void updateOrderStatus(Long id, ProcessStatus processStatus) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(id);
        orderInfo.setOrderStatus(processStatus.getOrderStatus().name());
        orderInfo.setProcessStatus(processStatus.name());
        baseMapper.updateById(orderInfo);
    }

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        QueryWrapper<OrderDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        List<OrderDetail> orderDetailList = orderDetailMapper.selectList(wrapper);
        orderInfo.setOrderDetailList(orderDetailList);
        return orderInfo;
    }
}
