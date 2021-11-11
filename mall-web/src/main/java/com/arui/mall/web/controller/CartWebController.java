package com.arui.mall.web.controller;

import com.arui.mall.common.result.R;
import com.arui.mall.feign.client.CartFeignClient;
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

    @Resource
    private CartFeignClient cartFeignClient;

    /**
     * 添加购物车，页面显示基本信息，
     * 远程调用cart服务，保存购物车信息到mysql和redis中（根据userId或者userTempId）
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

        // 获取userId 或 userTempId
        String userId = request.getHeader("userId");
        String userTempId = request.getHeader("userTempId");

        // 远程调用保存购物车信息
        cartFeignClient.addCart(Long.parseLong(skuId), Integer.parseInt(skuNum));
        return "cart/addCart";
    }
}
