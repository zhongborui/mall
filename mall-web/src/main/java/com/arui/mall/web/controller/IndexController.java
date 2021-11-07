package com.arui.mall.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.feign.client.ProductFeignClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

/**
 * 获取首页分类数据
 * @author ...
 */
@Controller
public class IndexController {

    @Resource
    private ProductFeignClient productFeignClient;

//    @GetMapping(value = {"/", "index.html", "index"})
//    public String index(Model model){
//        List<JSONObject> baseCategoryList = productFeignClient.getBaseCategoryList();
//        List<JSONObject> list = baseCategoryList;
//        model.addAttribute("list", list);
//        return "index/index";
//    }

}
