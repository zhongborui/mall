package com.arui.mall.core.order.controller;


import com.arui.mall.common.result.R;
import com.arui.mall.core.order.service.OrderInfoService;
import com.arui.mall.feign.client.CartFeignClient;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.feign.client.UserFeignClient;
import com.arui.mall.model.pojo.entity.*;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    private OrderInfoService orderInfoService;

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

    /**
     * 保存订单信息
     * @param request
     * @return
     */
    @PostMapping("submitOrder")
    public R submitOrder(@RequestBody OrderInfo orderInfo, HttpServletRequest request){
        String userId = request.getHeader("userId");
        String tradeNo = request.getParameter("tradeNo");
        // 幂等性，防止无刷新重复提交表单
        boolean flag = orderInfoService.checkTradNo(userId, tradeNo);
        if (!flag){
            // 不通过检验
            return R.error().message("不能无刷新提交表单");
        }
        // 校验价格和库存, 返回提示列表
        List<String> warningList = orderInfoService.checkPriceAndStock(orderInfo, userId);
        if (!CollectionUtils.isEmpty(warningList)){
            return R.error().message(StringUtils.join(warningList, ","));
        }

        // 以上没有问题，删除流水号
        orderInfoService.deleteTradNo(userId);
        // 保存订单到数据库， 返回订单id
        Long orderId = orderInfoService.saveOrderDetail(orderInfo, Long.parseLong(userId));
        return R.ok(orderId);
    }

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    @GetMapping("getOrderInfo/{orderId}")
    public OrderInfo getOrderInfo(@PathVariable Long orderId){
        return orderInfoService.getOrderInfo(orderId);
    }
}

