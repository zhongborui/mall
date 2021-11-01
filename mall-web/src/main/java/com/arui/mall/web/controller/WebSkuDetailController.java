package com.arui.mall.web.controller;

import com.alibaba.fastjson.JSON;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.entity.BaseCategoryView;
import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // sku基本信息
        SkuInfoVO skuDetail = productFeignClient.getSkuDetailById(skuId);
        map.put("skuInfo", skuDetail);

        // 获取三级分类信息
        Long category3Id = skuDetail.getCategory3Id();
        BaseCategoryView categoryView = productFeignClient.getCategoryViewByCategory3Id(category3Id);
        map.put("categoryView", categoryView);

        // 获取sku价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
        map.put("price", skuPrice);

        // 根据skuId，spuId查询spu销售属性和选中的sku销售属性
        Long productId = skuDetail.getSpuId();
        List<SpuSalePropertyName> spuSPNAndSkuSPNSelected = productFeignClient.getSpuSPNAndSkuSPNSelected(productId, skuId);
        map.put("spuSalePropertyList", spuSPNAndSkuSPNSelected);

        // 根据spuId查询spu销售属性组合对应的sku
        List<Map> spuSPVAndSkuMapping = productFeignClient.getSpuSPVAndSkuMapping(productId);
        HashMap<Object, Object> map2 = new HashMap<>();
        for (Map map1 : spuSPVAndSkuMapping) {
            map2.put(map1.get("sale_property_value_id"), map1.get("sku_id"));
        }
        map.put("salePropertyValueIdJson", JSON.toJSONString(map2));

        model.addAllAttributes(map);

        return "detail/index";
    }
}
