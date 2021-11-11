package com.arui.mall.web.controller;

import com.arui.mall.common.result.R;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author ...
 */

@Controller
public class CartWebController {

    @Resource
    private ProductFeignClient productFeignClient;

    /**
     * 添加购物车，页面显示基本信息
     * @param request
     * @return
     */
    @GetMapping("addCart.html")
    public String addCart(HttpServletRequest request){
        String skuId = request.getParameter("skuId");
        String skuNum = request.getParameter("skuNum");
        SkuInfoVO skuInfoVO = productFeignClient.getSkuDetailById(Long.parseLong(skuId));
        request.setAttribute("skuInfo", skuInfoVO);
        request.setAttribute("skuNum", skuNum);
        return "cart/addCart";
    }
}
