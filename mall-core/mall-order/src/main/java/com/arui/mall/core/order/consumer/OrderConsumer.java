package com.arui.mall.core.order.consumer;

import com.arui.mall.core.constant.MqConst;
import com.arui.mall.core.order.service.OrderInfoService;
import com.arui.mall.model.enums.OrderStatus;
import com.arui.mall.model.enums.ProcessStatus;
import com.arui.mall.model.pojo.entity.OrderInfo;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 模拟延迟队列消费者 10s钟后，订单还未被支付，将关闭订单
 *
 * @author ...
 */
@Component
public class OrderConsumer {

    @Resource
    private OrderInfoService orderInfoService;

    @SneakyThrows
    @RabbitListener(queues = MqConst.CANCEL_ORDER_QUEUE)
    public void orderConsumer(Long id, Message message, Channel channel){
        try {
            if (id != null){
                // 根据订单号查看订单的状态，如果还是未支付，则将订单关闭
                OrderInfo orderInfo = orderInfoService.checkOrderStatus(id);
                if (orderInfo.getOrderStatus().equals(ProcessStatus.UNPAID.getOrderStatus().name())) {
                    // 关闭订单
                    orderInfoService.updateOrderStatus(id, ProcessStatus.CLOSED);
                }
            }
            // 批量应答
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
