package com.arui.mall.feign.client;

import com.arui.mall.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author ...
 */
@FeignClient(value = "mall-cart")
public interface CartFeignClient {

    /**
     * 添加购物车信息
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("/cart/addCart/{skuId}/{skuNum}")
    public R addCart(@PathVariable Long skuId, @PathVariable Integer skuNum);
}
