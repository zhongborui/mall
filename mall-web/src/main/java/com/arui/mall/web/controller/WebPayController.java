package com.arui.mall.web.controller;

import com.arui.mall.feign.client.OrderFeignClient;
import com.arui.mall.model.pojo.entity.OrderInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * 支付web服务
 * @author ...
 */
@Controller
public class WebPayController {

    @Resource
    private OrderFeignClient orderFeignClient;

    /**
     * 显示支付页面
     * @param orderId
     * @param model
     * @return
     */
    @GetMapping("pay.html")
    public String pay(@RequestParam Long orderId, Model model){
        System.out.println(orderId);
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        model.addAttribute("orderInfo", orderInfo);
        return "payment/pay";
    }

    /**
     * 支付成功页面
     * @return
     */
    @GetMapping("alipay/success.html")
    public String success(){
        return "payment/success";
    }

}
