package com.arui.mall.feign.client;

import com.arui.mall.feign.fallback.ProductFeignClientFallback;
import com.arui.mall.model.pojo.entity.BaseCategoryView;
import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
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
    public SkuInfoVO getSkuDetailById(@PathVariable Long skuId);

    /**
     * 根据三级分类id获取sku的分类信息
     *
     * @param category3Id
     * @return
     */
    @ApiOperation(value = "根据三级分类id获取sku的分类信息")
    @GetMapping("CategoryView/{category3Id}")
    public BaseCategoryView getCategoryViewByCategory3Id(
            @PathVariable Long category3Id
    ) ;

    /**
     * 根据skuId获取sku价格
     *
     * @param skuId
     * @return
     */
    @ApiOperation(value = "根据skuId获取sku价格")
    @GetMapping("skuPrice/{skuId}")
    public BigDecimal getSkuPrice(
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
    @ApiOperation(value = "根据skuId，spuId查询spu销售属性和选中的sku销售属性")
    @GetMapping("getSpuSPNAndSkuSPNSelected/{spuId}/{skuId}")
    public List<SpuSalePropertyName> getSpuSPNAndSkuSPNSelected(
            @ApiParam(value = "spuId")

            @PathVariable Long spuId,
            @ApiParam(value = "skuId")
            @PathVariable Long skuId
    ) ;

    /**
     * 根据spuId查询spu销售属性组合对应的sku
     *
     * @param spuId
     * @return
     */
    @GetMapping("getSpuSPVAndSkuMapping/{spuId}")
    @ApiOperation(value = "根据spuId查询spu销售属性组合对应的sku")
    public List<Map> getSpuSPVAndSkuMapping(
            @ApiParam(value = "spuId")
            @PathVariable Long spuId);
}
