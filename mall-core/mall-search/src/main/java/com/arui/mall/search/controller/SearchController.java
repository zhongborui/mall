package com.arui.mall.search.controller;

import com.arui.mall.common.result.R;
import com.arui.mall.model.search.Product;
import com.arui.mall.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author ...
 */
@Api(tags = "搜索相关api")
@RestController
@RequestMapping("/search")
public class SearchController {
    @Resource
    private ElasticsearchRestTemplate esRestTemplate;

    @Resource
    private SearchService searchService;

    @ApiOperation(value = "建立es索引和映射")
    @GetMapping("createIndex")
    public R createIndex() {
        esRestTemplate.createIndex(Product.class);
        esRestTemplate.putMapping(Product.class);
        return R.ok();
    }

    /**
     * es上架
     * @param skuId
     * @return
     */
    @GetMapping("onSale/{skuId}")
    public R onSale(@PathVariable Long skuId){
        searchService.onSale(skuId);
        return R.ok();
    }

    /**
     * es下架
     * @param skuId
     * @return
     */
    @GetMapping("offSale/{skuId}")
    public R offSale(@PathVariable Long skuId){
        searchService.offSale(skuId);
        return R.ok();
    }

    /**
     * 增加热度
     * @param skuId
     * @return
     */
    @GetMapping("incrHostScore/{skuId}")
    public R incrHostScore(@PathVariable Long skuId){
        searchService.incrHostScore(skuId);
        return R.ok();
    }
}
