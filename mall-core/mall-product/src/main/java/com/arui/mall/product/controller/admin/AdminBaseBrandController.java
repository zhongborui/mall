package com.arui.mall.product.controller.admin;


import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.entity.BaseBrand;
import com.arui.mall.product.service.BaseBrandService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Api(tags = "后台管理商品品牌列表")
@RestController
@RequestMapping("/product")
public class AdminBaseBrandController {

    @Resource
    private BaseBrandService baseBrandService;

    /**
     * //http://127.0.0.1/product/brand/queryBrandByPage/1/10
     * @param currentPage
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "商品品牌列表分页")
    @GetMapping("/brand/queryBrandByPage/{currentPage}/{pageSize}")
    public R queryBrandByPage(
            @ApiParam(value = "当前页", required = true, example = "1")
            @PathVariable Long currentPage,

            @ApiParam(value = "每页多少条", required = true, example = "10")
            @PathVariable Long pageSize){
        Page<BaseBrand> baseBrandPage = new Page<>(currentPage, pageSize);
        baseBrandService.page(baseBrandPage, null);
        return R.ok(baseBrandPage);
    }

    /**http://127.0.0.1/product/brand
     * 添加商品品牌信息
     * @param baseBrand
     * @return
     */
    @ApiOperation(value = "添加商品品牌信息")
    @PostMapping("/brand")
    public R saveBrand(
            @ApiParam(value = "保存品牌信息的表单")
            @RequestBody BaseBrand baseBrand){
        baseBrandService.save(baseBrand);
        return R.ok();
    }

    @ApiOperation(value = "修改商品品牌信息")
    @PutMapping("/brand")
    public R updateBrand(
            @ApiParam(value = "保存品牌信息的表单")
            @RequestBody BaseBrand baseBrand){
        baseBrandService.updateById(baseBrand);
        return R.ok();
    }


    @ApiOperation(value = "删除商品品牌信息")
    @DeleteMapping("/brand/{id}")
    public R deleteBrand(
            @ApiParam(value = "品牌id")
            @PathVariable Long id){
        baseBrandService.removeById(id);
        return R.ok();
    }
}

