package com.arui.mall.core.order.controller;


import com.arui.mall.common.result.R;
import com.arui.mall.feign.client.CartFeignClient;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.feign.client.UserFeignClient;
import com.arui.mall.model.pojo.entity.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

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

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 获取订单确认页面数据
     * @return
     */
    @GetMapping("confirm")
    public R showConfirmPage(HttpServletRequest request){
        String userId = request.getHeader("userId");
        Map<String, Object> resMap = new HashMap<>();
        // 已选购物项列表
        List<CartInfo> cartListSelected = cartFeignClient.getCartListSelected(Long.parseLong(userId));
        // 订单项列表
        List<OrderDetail> orderDetailList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(cartListSelected)){
            for (CartInfo cartInfo : cartListSelected) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                orderDetail.setSkuNum(cartInfo.getSkuNum()+"");
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetailList.add(orderDetail);
            }

        }
        resMap.put("detailArrayList", orderDetailList);
        // 收件人地址信息
        Long userIdP = Long.parseLong(userId);
        List<UserAddress> userAddressList = userFeignClient.getUserAddressList(userIdP);
        resMap.put("userAddressList", userAddressList);
        // 返回用户基本信息
//        UserInfo userInfo = userFeignClient.getUserInfo(userIdP);
//        resMap.put("userInfo", userInfo);
        // 商品总件数
        int totalNum = 0;
        // 商品总金额
        BigDecimal totalPrice = new BigDecimal(0);
        for (CartInfo datum : cartListSelected) {
            totalNum += datum.getSkuNum();
            BigDecimal onePrice = new BigDecimal(String.valueOf(datum.getCartPrice()));
            BigDecimal oneTotal = onePrice.multiply(new BigDecimal(datum.getSkuNum()));
            totalPrice = totalPrice.add(oneTotal);
        }
        resMap.put("totalNum", totalNum);
        resMap.put("totalMoney", totalPrice);
        // 流水号
        // 生成的流水号 防止表单重复提交
        String tradeNo = UUID.randomUUID().toString();
        // 定义key
        String tradeNoKey = "user:"+userId+":tradeNo";
        redisTemplate.opsForValue().set(tradeNoKey,tradeNo);
        resMap.put("tradeNo",tradeNo);
        return R.ok(resMap);
    }

    @PostMapping("submitOrder")
    public R submitOrder(HttpServletRequest request){
        String tradeNo = request.getParameter("tradeNo");

        return R.ok();
    }
}

