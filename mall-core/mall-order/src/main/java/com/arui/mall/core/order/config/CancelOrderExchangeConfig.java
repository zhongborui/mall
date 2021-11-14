package com.arui.mall.core.order.config;

import com.arui.mall.core.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 延迟插件延迟队列配置类
 * @author ...
 */
@Configuration
public class CancelOrderExchangeConfig {

    @Bean
    public Queue queue(){
        return QueueBuilder.nonDurable(MqConst.CANCEL_ORDER_QUEUE).build();
    }

    @Bean
    public CustomExchange customExchange(){
        Map<String, Object> args = new HashMap<>();
        // 设置自定义交换机类型
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MqConst.CANCEL_ORDER_EXCHANGE,
                "x-delayed-message",
                false,
                false,
                args);
    }

    @Bean
    public Binding binding(Queue queue, CustomExchange customExchange){
        return BindingBuilder.bind(queue).to(customExchange).
                with(MqConst.CANCEL_ORDER_ROUTE_KEY).noargs();
    }
}
