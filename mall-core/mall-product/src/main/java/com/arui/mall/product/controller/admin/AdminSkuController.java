package com.arui.mall.product.controller.admin;


import com.arui.mall.common.result.R;
import com.arui.mall.feign.client.SearchFeignClient;
import com.arui.mall.model.pojo.entity.SkuInfo;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.arui.mall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 库存单元表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Api(tags = "后台管理商品sku")
@RestController
@RequestMapping("/product")
public class AdminSkuController {

    @Resource
    private SkuInfoService skuInfoService;

    @Resource
    private SearchFeignClient searchFeignClient;

    /**
     *     //http://127.0.0.1/product/saveSkuInfo
     * @param skuInfoVO
     * @return
     */
    @ApiOperation(value = "新增sku")
    @PostMapping("/saveSkuInfo")
    public R saveSkuInfo(
            @ApiParam(value = "sku信息表单")
            @RequestBody SkuInfoVO skuInfoVO){
        skuInfoService.saveSkuInfo(skuInfoVO);
        return R.ok();
    }

    /**
     * http://127.0.0.1/product/querySkuInfoByPage/1/10
     * @param currentPageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "sku分页展示")
    @GetMapping("querySkuInfoByPage/{currentPageNum}/{pageSize}")
    public R querySkuInfoByPage(
            @PathVariable Long currentPageNum,
            @PathVariable Long pageSize
    ){
        Page<SkuInfo> skuInfoPage = new Page<>(currentPageNum, pageSize);
        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.orderByDesc("id");
        IPage<SkuInfo> page = skuInfoService.page(skuInfoPage, skuInfoQueryWrapper);
        return R.ok(page);
    }

    /**
     * //http://127.0.0.1/product/onSale/40
     * @param skuId
     * @return
     */
    @ApiOperation(value = "sku上架")
    @GetMapping("onSale/{skuId}")
    public R onSale(@PathVariable Long skuId){
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoService.updateById(skuInfo);
        searchFeignClient.onSale(skuId);
        return R.ok();
    }

    @ApiOperation(value = "sku下架")
    @GetMapping("offSale/{skuId}")
    public R offSale(@PathVariable Long skuId){
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoService.updateById(skuInfo);
        searchFeignClient.offSale(skuId);
        return R.ok();
    }
}

