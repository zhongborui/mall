package com.arui.mall.product.controller.admin;


import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.entity.BaseCategory1;
import com.arui.mall.model.pojo.entity.BaseCategory2;
import com.arui.mall.model.pojo.entity.BaseCategory3;
import com.arui.mall.product.service.BaseCategory1Service;
import com.arui.mall.product.service.BaseCategory2Service;
import com.arui.mall.product.service.BaseCategory3Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 一级分类表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Api(tags = "后台管理商品三级分类")
@RestController
@RequestMapping("/product")
public class AdminBaseCategoryController {

    @Resource
    private BaseCategory1Service baseCategory1Service;

    @Resource
    private BaseCategory2Service baseCategory2Service;

    @Resource
    private BaseCategory3Service baseCategory3Service;

    @ApiOperation(value = "查询商品一级分类列表")
    @GetMapping("/getCategory1")
    public R getCategory1(){
        List<BaseCategory1> baseCategory1List = baseCategory1Service.getCategory1();
        return R.ok(baseCategory1List);
    }

    @ApiOperation(value = "根据一级id，查询商品二级分类列表")
    @GetMapping("/getCategory2/{category1ID}")
    public R getCategory2(
            @ApiParam(value = "一级id", required = true, example = "0")
            @PathVariable Long category1ID
    ){
        List<BaseCategory2> baseCategory2List = baseCategory2Service.getCategory2(category1ID);
        return R.ok(baseCategory2List);
    }

    @ApiOperation(value = "根据二级id,查询商品三级分类列表")
    @GetMapping("/getCategory3/{category2ID}")
    public R getCategory3(
            @ApiParam(value = "二级id", required = true, example = "0")
            @PathVariable Long category2ID
    ){
        List<BaseCategory3> baseCategory3List = baseCategory3Service.getCategory3(category2ID);
        return R.ok(baseCategory3List);
    }

}

