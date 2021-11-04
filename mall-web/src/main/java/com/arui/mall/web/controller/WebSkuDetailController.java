package com.arui.mall.web.controller;

import com.alibaba.fastjson.JSON;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.entity.BaseCategoryView;
import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.arui.mall.web.executor.MyThreadExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * 服务端渲染商品详情页
 * @author ...
 */
@Controller
public class WebSkuDetailController {

    @Resource
    private ProductFeignClient productFeignClient;

    @RequestMapping("{skuId}.html")
    public String getSkuDetail(@PathVariable Long skuId, Model model){
        HashMap<String, Object> map = new HashMap<>();
        // 首页数据异步编排

        // sku基本信息
        CompletableFuture<SkuInfoVO> skuDetailSupplyAsync = CompletableFuture.supplyAsync(() -> {
            SkuInfoVO skuDetail = productFeignClient.getSkuDetailById(skuId);
            map.put("skuInfo", skuDetail);
            return skuDetail;
        }, MyThreadExecutor.getInstance());

        // 获取三级分类信息
        CompletableFuture<Void> categoryViewFuture = skuDetailSupplyAsync.thenAcceptAsync(skuInfoVO -> {
            Long category3Id = skuInfoVO.getCategory3Id();
            BaseCategoryView categoryView = productFeignClient.getCategoryViewByCategory3Id(category3Id);
            map.put("categoryView", categoryView);
        }, MyThreadExecutor.getInstance());

        // 根据skuId，spuId查询spu销售属性和选中的sku销售属性
        CompletableFuture<Void> spuSalePropertyListFuture = skuDetailSupplyAsync.thenAcceptAsync(skuInfoVO -> {
            Long productId = skuInfoVO.getSpuId();
            List<SpuSalePropertyName> spuSPNAndSkuSPNSelected = productFeignClient.getSpuSPNAndSkuSPNSelected(productId, skuId);
            map.put("spuSalePropertyList", spuSPNAndSkuSPNSelected);
        }, MyThreadExecutor.getInstance());

        // 获取sku价格
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            map.put("price", skuPrice);
        }, MyThreadExecutor.getInstance());

        // 根据spuId查询spu销售属性组合对应的sku
        CompletableFuture<Void> spuSPVAndSkuMappingFuture = skuDetailSupplyAsync.thenAcceptAsync(skuInfoVO -> {
            Long productId = skuInfoVO.getSpuId();
            List<Map> spuSPVAndSkuMapping = productFeignClient.getSpuSPVAndSkuMapping(productId);
            HashMap<Object, Object> map2 = new HashMap<>();
            for (Map map1 : spuSPVAndSkuMapping) {
                map2.put(map1.get("sale_property_value_id"), map1.get("sku_id"));
            }
            map.put("salePropertyValueIdJson", JSON.toJSONString(map2));
        }, MyThreadExecutor.getInstance());

        // 组合future
        CompletableFuture.allOf(
                skuDetailSupplyAsync,
                categoryViewFuture,
                spuSalePropertyListFuture,
                priceFuture,
                spuSPVAndSkuMappingFuture
        ).join();

        model.addAllAttributes(map);
        return "detail/index";
    }
}
