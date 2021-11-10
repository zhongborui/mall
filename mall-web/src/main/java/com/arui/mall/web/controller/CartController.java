package com.arui.mall.web.controller;

import com.arui.mall.common.util.AuthContextHolder;
import com.arui.mall.feign.client.CartFeignClient;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.arui.mall.web.executor.MyThreadExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * @author ...
 */
@Controller
public class CartController {

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private CartFeignClient cartFeignClient;

    @GetMapping("/addCart.html")
    public String addCart(@RequestParam Long skuId, @RequestParam Integer skuNum, HttpServletRequest request){

        // 远程调用cart服务
        cartFeignClient.addCart(skuId, skuNum);

        // sku基本信息
        SkuInfoVO skuInfo = productFeignClient.getSkuDetailById(skuId);
        // skuNum
        request.setAttribute("skuInfo", skuInfo);
        request.setAttribute("skuNum", skuNum);

        return "cart/addCart";
    }
}
