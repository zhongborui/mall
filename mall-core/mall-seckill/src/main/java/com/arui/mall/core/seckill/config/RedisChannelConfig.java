package com.arui.mall.core.seckill.config;

import com.arui.mall.core.constant.RedisConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author ...
 */
@Configuration
public class RedisChannelConfig {

    /**
     * 自定义处理消息的监听器
     * @param myRedisMessageReceiver
     * @return
     */
    @Bean
    MessageListenerAdapter listenerAdapter(MyRedisMessageReceiver myRedisMessageReceiver){
        return new MessageListenerAdapter(myRedisMessageReceiver, "receiveChanelMessage");
    }

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory factory, MessageListenerAdapter listenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        // 订阅哪个channel（即消息在那个channel发布
        container.addMessageListener(listenerAdapter, new PatternTopic(RedisConstant.PREPARE_PUB_SUB_SECKILL));
        return container;
    }
}
