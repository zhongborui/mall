package com.arui.mall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 服务端渲染商品详情页
 * @author ...
 */
@Controller
public class WebSkuDetailController {

    @RequestMapping("{skuId}.html")
    public String getSkuDetail(){
        return "detail/index";
    }
}
