package com.arui.mall.product.controller.web;


import com.arui.mall.model.pojo.entity.BaseCategoryView;
import com.arui.mall.model.pojo.entity.SkuImage;
import com.arui.mall.model.pojo.entity.SkuInfo;
import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.arui.mall.product.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.print.attribute.standard.NumberUp;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 库存单元表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Api(tags = "返回Web的sku接口")
@RestController
@RequestMapping("/web/sku")
public class WebSkuController {

    @Resource
    private SkuImageDetailService skuImageDetailService;

    @Resource
    private SkuInfoService skuInfoService;

    @Resource
    private BaseCategoryViewService categoryViewService;

    @Resource
    private SpuSalePropertyNameService spuSalePropertyNameService;

    @Resource
    private SkuSalePropertyValueService skuSalePropertyValueService;

    /**
     * 根据skuId获取sku的基本信息
     * @param skuId
     * @return
     */
    @ApiOperation(value = "根据skuId获取sku的基本信息")
    @GetMapping("skuDetail/{skuId}")
    public SkuInfoVO getSkuDetailById(
            @ApiParam(value = "skuId")
            @PathVariable Long skuId){
        SkuInfoVO skuInfoVO = skuInfoService.getSkuDetailById(skuId);
        return skuInfoVO;
    }

    /**
     * 根据三级分类id获取sku的分类信息
     * @param category3Id
     * @return
     */
    @ApiOperation(value = "根据三级分类id获取sku的分类信息")
    @GetMapping("CategoryView/{category3Id}")
    public BaseCategoryView getCategoryViewByCategory3Id(
            @ApiParam(value = "三级分类id")
            @PathVariable Long category3Id
    ){
      return categoryViewService.getById(category3Id);
    }

    /**
     * 根据skuId获取sku价格
     * @param skuId
     * @return
     */
    @ApiOperation(value = "根据skuId获取sku价格")
    @GetMapping("skuPrice/{skuId}")
    public BigDecimal getSkuPrice(
            @ApiParam(value = "skuId")
            @PathVariable Long skuId
    ){
        BigDecimal skuPrice = skuInfoService.getSkuPrice(skuId);
        return skuPrice;
    }

    /**
     * 根据skuId，spuId查询spu销售属性和选中的sku销售属性
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
    ){
        List<SpuSalePropertyName> spuSalePropertyName = spuSalePropertyNameService.getSpuSPNAndSkuSPNSelected(spuId, skuId);
        return spuSalePropertyName;
    }

    /**
     * 根据spuId查询spu销售属性组合对应的sku
     * @param spuId
     * @return
     */
    @GetMapping("getSpuSPVAndSkuMapping/{spuId}")
    @ApiOperation(value = "根据spuId查询spu销售属性组合对应的sku")
    public List<Map> getSpuSPVAndSkuMapping(
            @ApiParam(value = "spuId")
            @PathVariable Long spuId){
        List<Map> map =  skuSalePropertyValueService.getSpuSPVAndSkuMapping(spuId);
        return map;
    }
}

