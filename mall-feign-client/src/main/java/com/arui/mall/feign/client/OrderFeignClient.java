package com.arui.mall.feign.client;

import com.arui.mall.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

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
}
