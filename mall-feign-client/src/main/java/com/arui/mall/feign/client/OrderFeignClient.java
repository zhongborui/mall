package com.arui.mall.feign.client;

import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.entity.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author ...
 */
@FeignClient(value = "mall-order")
public interface OrderFeignClient {
    /**
     * 获取订单确认页面数据
     * @return
     */
    @GetMapping("/order/confirm")
    public R showConfirmPage();

    /**
     * 获取订单信息
     * @param orderId
     * @return
     */
    @GetMapping("/order/getOrderInfo/{orderId}")
    public OrderInfo getOrderInfo(@PathVariable Long orderId);
}
