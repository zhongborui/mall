package com.arui.mall.product.controller.admin;


import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.arui.mall.product.service.SkuInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    //http://127.0.0.1/product/saveSkuInfo

    @Resource
    private SkuInfoService skuInfoService;

    @ApiOperation(value = "新增sku")
    @PostMapping("/saveSkuInfo")
    public R saveSkuInfo(
            @ApiParam(value = "sku信息表单")
            @RequestBody SkuInfoVO skuInfoVO){
        skuInfoService.saveSkuInfo(skuInfoVO);
        return R.ok();
    }
}

