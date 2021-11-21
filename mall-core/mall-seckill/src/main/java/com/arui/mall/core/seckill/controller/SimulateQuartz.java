package com.arui.mall.core.seckill.controller;

import com.arui.mall.core.constant.MqConst;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 为方便测试，模拟定时任务
 * @author ...
 */
@Api(tags = "模拟定时任务")
@RestController
@RequestMapping("/seckill")
public class SimulateQuartz {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 定时发送消息，通知上架接口，扫描数据库，添加商品到redis
     * @return
     */
    @ApiOperation(value = "模拟定时通知上架接口")
    @GetMapping("sendMsgToScanSeckill")
    public String sendMsgToScanSeckill(){
        rabbitTemplate.convertAndSend(MqConst.SCAN_SECKILL_EXCHANGE, MqConst.SCAN_SECKILL_ROUTE_KEY, "");
        return "success";
    }
}
