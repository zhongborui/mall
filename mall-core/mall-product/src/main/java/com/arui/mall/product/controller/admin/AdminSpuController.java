package com.arui.mall.product.controller.admin;


import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.entity.BaseSaleProperty;
import com.arui.mall.model.pojo.entity.SpuImage;
import com.arui.mall.model.pojo.entity.SpuInfo;
import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.arui.mall.model.pojo.vo.SpuInfoVO;
import com.arui.mall.product.service.BaseSalePropertyService;
import com.arui.mall.product.service.SpuImageService;
import com.arui.mall.product.service.SpuInfoService;
import com.arui.mall.product.service.SpuSalePropertyNameService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Api(tags = "后台管理商品属性SPU管理")
@RestController
@RequestMapping("/product")
public class AdminSpuController {

    @Resource
    private SpuInfoService spuInfoService;

    @Resource
    private SpuSalePropertyNameService spuSalePropertyNameService;

    @Resource
    private BaseSalePropertyService baseSalePropertyService;

    @Resource
    private SpuImageService spuImageService;

    // http://127.0.0.1/product/queryProductSpuByPage/1/10/61

    @ApiOperation(value = "返回商品属性SPU分页列表")
    @GetMapping("/queryProductSpuByPage/{currentPage}/{pageSize}/{catetory3Id}")
    public R queryProductSpuByPage(
            @ApiParam(value = "当前页", required = true, example = "1")
            @PathVariable Long currentPage,

            @ApiParam(value = "每页条数", required = true, example = "10")
            @PathVariable Long pageSize,

            @ApiParam(value = "三级id", required = true)
            @PathVariable Long catetory3Id){
        Page<SpuInfo> spuInfoPage = new Page<>(currentPage, pageSize);
        QueryWrapper<SpuInfo> spuInfoQueryWrapper = new QueryWrapper<>();
        spuInfoQueryWrapper.eq("category3_id", catetory3Id);
        IPage<SpuInfo> page = spuInfoService.page(spuInfoPage, spuInfoQueryWrapper);
        return R.ok(page);
    }

    /**http://127.0.0.1/product/queryAllSaleProperty
     * 返回SPU销售属性名称
     * @return
     */
    @ApiOperation(value = "返回SPU销售属性名称")
    @GetMapping("/queryAllSaleProperty")
    public R queryAllSaleProperty(){
        List<BaseSaleProperty> list = baseSalePropertyService.list(null);
        return R.ok(list);
    }

    /**http://127.0.0.1/product/saveProductSpu
     *
     * @param spuInfoVO
     * @return
     */
    @ApiOperation(value = "新增SPU")
    @PostMapping("/saveProductSpu")
    public R saveProductSpu(
            @ApiParam(value = "SPU属性表单")
            @RequestBody SpuInfoVO spuInfoVO){
        spuInfoService.saveSpu(spuInfoVO);
        return R.ok();
    }

    //http://127.0.0.1/product/querySalePropertyByProductId/20
    @ApiOperation(value = "根据productId查询spu销售属性")
    @GetMapping("querySalePropertyByProductId/{productId}")
    public R querySalePropertyByProductId(
            @ApiParam(value = "spuId")
            @PathVariable Long productId){
        List<SpuSalePropertyName> list = spuSalePropertyNameService.querySalePropertyByProductId(productId);
        return R.ok(list);
    }

    //http://127.0.0.1/product/queryProductImageByProductId/20
    @ApiOperation(value = "根据spuId查询spu照片集合")
    @GetMapping("/queryProductImageByProductId/{productId}")
    public R queryProductImageByProductId(
            @ApiParam(value = "spuId")
            @PathVariable Long productId){
        QueryWrapper<SpuImage> spuImageQueryWrapper = new QueryWrapper<>();
        spuImageQueryWrapper.eq("spu_id", productId);
        List<SpuImage> list = spuImageService.list(spuImageQueryWrapper);
        return R.ok(list);
    }
}

