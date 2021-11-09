package com.arui.mall.web.controller;

import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author ...
 */
@Controller
public class CartController {

    @Resource
    private ProductFeignClient productFeignClient;

    @GetMapping("/addCart.html")
    public String addCart(@RequestParam Long skuId, @RequestParam Long skuNum, HttpServletRequest request){
        // sku基本信息
        SkuInfoVO skuInfo = productFeignClient.getSkuDetailById(skuId);
        // skuNum
        request.setAttribute("skuInfo", skuInfo);
        request.setAttribute("skuNum", skuNum);
        return "cart/addCart";
    }
}
