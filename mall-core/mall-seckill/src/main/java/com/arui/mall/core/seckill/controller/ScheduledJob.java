package com.arui.mall.core.seckill.controller;

import com.arui.mall.core.constant.MqConst;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * springboot定时任务
 * @author ...
 */
@EnableScheduling
@Component
public class ScheduledJob {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0 0 1 * * ?")
    public void onSaleTask() {
        // 发送的消息
        rabbitTemplate.convertAndSend(MqConst.SCAN_SECKILL_EXCHANGE, MqConst.SCAN_SECKILL_ROUTE_KEY, "");
    }
}
