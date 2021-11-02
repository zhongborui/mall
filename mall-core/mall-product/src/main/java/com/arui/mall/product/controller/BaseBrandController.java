package com.arui.mall.product.controller;


import com.arui.mall.product.service.BaseBrandService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@RestController
@RequestMapping("/base-brand")
public class BaseBrandController {

    @Resource
    private BaseBrandService baseBrandService;

    @GetMapping("/lock")
    public String testLocalLock(){
        baseBrandService.testLocalLock();
        return "success";
    }
}

