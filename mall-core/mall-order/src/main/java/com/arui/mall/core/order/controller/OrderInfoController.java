package com.arui.mall.core.order.controller;


import com.arui.mall.common.result.R;
import com.arui.mall.feign.client.CartFeignClient;
import com.arui.mall.feign.client.ProductFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 订单表 订单表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@RestController
@RequestMapping("/order")
public class OrderInfoController {
    @Resource
    private CartFeignClient cartFeignClient;

    @GetMapping("/confirm")
    public R showConfirmPage(){
        Map<String, Object> resMap = new HashMap<>();
        R cartList = cartFeignClient.getCartList();
        resMap.put("detailArrayList", cartList.getData());
        return R.ok(resMap);
    }
}

