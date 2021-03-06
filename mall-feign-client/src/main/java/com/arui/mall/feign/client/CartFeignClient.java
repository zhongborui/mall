package com.arui.mall.feign.client;

import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.entity.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author ...
 */
@FeignClient(value = "mall-cart")
public interface CartFeignClient {
    /**
     * 保存购物车信息，如果skuId和termId都有，优先保存skuId
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("/cart/addCart/{skuId}/{skuNum}")
    public R addCart(@PathVariable Long skuId, @PathVariable Integer skuNum);

    /**
     * 展示购物车列表, 未登录的，如果再购物车列表登录，需要合并，userId和tempId
     * @param
     * @return
     */
    @GetMapping("/cart/getCartList")
    public R getCartList();

    /**
     *  勾选
     * @param userId
     * @return
     */
    @GetMapping("/cart/getCartListSelected/{userId}")
    public List<CartInfo> getCartListSelected(@PathVariable Long userId);
}

