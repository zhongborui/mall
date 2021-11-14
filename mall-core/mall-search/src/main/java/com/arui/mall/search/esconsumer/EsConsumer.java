package com.arui.mall.search.esconsumer;

import com.arui.mall.core.constant.MqConst;
import com.arui.mall.search.service.SearchService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * es mq消费者
 * @author ...
 */
@Component
public class EsConsumer {

    @Resource
    private SearchService searchService;

    /**
     * 上架商品
     * @param skuId
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConst.ON_SALE_QUEUE, durable = "false"),
            exchange = @Exchange(name = MqConst.ON_OFF_SALE_EXCHANGE),
            key = {MqConst.ON_SALE_ROUTING_KEY}
    ))
    public void onSale(Long skuId, Message message, Channel channel){
        try {
            if (skuId != null){
                searchService.onSale(skuId);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (IOException e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), true, true);
        }
    }


    /**
     * 下架商品
     * @param skuId
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = MqConst.OFF_SALE_QUEUE, durable = "false"),
            exchange = @Exchange(name = MqConst.ON_OFF_SALE_EXCHANGE),
            key = {MqConst.OFF_SALE_ROUTING_KEY}
    ))
    public void offSale(Long skuId, Message message, Channel channel){
        try {
            if (skuId != null){
                searchService.offSale(skuId);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (IOException e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), true, true);
        }
    }
}
