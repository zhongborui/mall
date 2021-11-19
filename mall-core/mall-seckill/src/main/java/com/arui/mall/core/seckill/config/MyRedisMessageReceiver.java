package com.arui.mall.core.seckill.config;

import com.arui.mall.core.constant.RedisConstant;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 自定义处理消息的类
 * @author ...
 */
@Component
public class MyRedisMessageReceiver {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 处理消息的方法
     * * @param message
     */
    public void receiveChanelMessage(String message){
        if (!StringUtils.isEmpty(message)) {
            // 解析获取的消息
            String newMessage = message.replaceAll("\"", "");
            String[] splitMessages = newMessage.split(":");
            if (splitMessages.length == 2){
                redisTemplate.opsForValue().set(RedisConstant.SECKILL_STATE + splitMessages[0], splitMessages[1]);
            }
        }
    }
}
