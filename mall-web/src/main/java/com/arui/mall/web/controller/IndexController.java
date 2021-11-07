package com.arui.mall.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.common.result.R;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.feign.client.SearchFeignClient;
import com.arui.mall.model.search.SearchParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 获取首页分类数据
 * @author ...
 */
@Controller
public class IndexController {

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private SearchFeignClient searchFeignClient;

//    @GetMapping(value = {"/", "index.html", "index"})
//    public String index(Model model){
//        List<JSONObject> baseCategoryList = productFeignClient.getBaseCategoryList();
//        List<JSONObject> list = baseCategoryList;
//        model.addAttribute("list", list);
//        return "index/index";
//    }

    @GetMapping("/search.html")
    public String a(SearchParam searchParam, Model model){
        R<Map> r = searchFeignClient.searchProduct(searchParam);
        model.addAllAttributes(r.getData());
        return "search/index";
    }

}
