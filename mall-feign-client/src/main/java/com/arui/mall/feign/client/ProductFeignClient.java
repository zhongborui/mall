package com.arui.mall.feign.client;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.feign.fallback.ProductFeignClientFallback;
import com.arui.mall.model.pojo.entity.BaseCategoryView;
import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author ...
 */
@FeignClient(value = "mall-product", fallback = ProductFeignClientFallback.class)
public interface ProductFeignClient {
    /**
     *
     * @param skuId
     * @return
     */
    @GetMapping("/web/sku/skuDetail/{skuId}")
     SkuInfoVO getSkuDetailById(@PathVariable Long skuId);

    /**
     * 根据三级分类id获取sku的分类信息
     *
     * @param category3Id
     * @return
     */
    @GetMapping("/web/sku/CategoryView/{category3Id}")
     BaseCategoryView getCategoryViewByCategory3Id(
            @PathVariable Long category3Id
    ) ;

    /**
     * 根据skuId获取sku价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/web/sku/skuPrice/{skuId}")
     BigDecimal getSkuPrice(
            @ApiParam(value = "skuId")
            @PathVariable Long skuId
    ) ;

    /**
     * 根据skuId，spuId查询spu销售属性和选中的sku销售属性
     *
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("/web/sku/getSpuSPNAndSkuSPNSelected/{spuId}/{skuId}")
     List<SpuSalePropertyName> getSpuSPNAndSkuSPNSelected(
            @PathVariable Long spuId,
            @PathVariable Long skuId
    ) ;

    /**
     * 根据spuId查询spu销售属性组合对应的sku
     *
     * @param spuId
     * @return
     */
    @GetMapping("/web/sku/getSpuSPVAndSkuMapping/{spuId}")
     List<Map> getSpuSPVAndSkuMapping(
            @PathVariable Long spuId);


    /**
     * 查询首页三级分类信息
     * @return
     */
    @GetMapping("/getBaseCategoryList")
     List<JSONObject> getBaseCategoryList();
}