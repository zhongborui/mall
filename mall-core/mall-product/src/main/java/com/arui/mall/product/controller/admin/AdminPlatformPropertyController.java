package com.arui.mall.product.controller.admin;


import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.vo.PlatformPropertyVO;
import com.arui.mall.model.pojo.vo.PlatformPropertyValueVO;
import com.arui.mall.product.service.PlatformPropertyNameService;
import com.arui.mall.product.service.PlatformPropertyValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 属性表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@RestController
@RequestMapping("/product")
@Api(tags = "后台管理商品平台属性")
public class AdminPlatformPropertyController {

    @Resource
    private PlatformPropertyNameService platformPropertyNameService;

    @Resource
    private PlatformPropertyValueService platformPropertyValueService;

    @ApiOperation(value = "根据三级id查询商品平台属性")
    @GetMapping("/getPlatformPropertyByCategoryId/{category1ID}/{category2ID}/{category3ID}")
    public R getPlatformPropertyByCategoryId(
            @ApiParam(value = "一级id", example = "0")
            @PathVariable Long category1ID,

            @ApiParam(value = "二级id", example = "0")
            @PathVariable Long category2ID,

            @ApiParam(value = "三级id", example = "0")
            @PathVariable Long category3ID) {
        List<PlatformPropertyVO> platformPropertyVOList = platformPropertyNameService.getPlatformPropertyByCategoryId(category1ID, category2ID, category3ID);
        return R.ok(platformPropertyVOList);
    }

    /**
     * http://127.0.0.1/product/getPropertyValueByPropertyKeyId/4
     * @param propertyKeyId
     * @return
     */
    @GetMapping("/getPropertyValueByPropertyKeyId/{propertyKeyId}")
    @ApiOperation(value = "根据平台属性key的id，查询平台属性value")
    public R getPropertyValueByPropertyKeyId(
            @ApiParam(value = "平台属性key的id", required = true)
            @PathVariable Long propertyKeyId){
        List<PlatformPropertyValueVO> platformPropertyValueListVO = platformPropertyValueService.getPropertyValueByPropertyKeyId(propertyKeyId);
        return R.ok(platformPropertyValueListVO);
    }

    /**
     * // http://127.0.0.1/product/savePlatformProperty 保存平台属性
     * @param platformPropertyVO
     * @return
     */
    @ApiOperation(value = "新增或者修改平台属性")
    @PostMapping("/savePlatformProperty")
    public R savePlatformProperty(
            @ApiParam(value = "保存平台属性表单", required = true)
            @RequestBody PlatformPropertyVO platformPropertyVO){
        platformPropertyNameService.savePlatformProperty(platformPropertyVO);
        return R.ok();
    }
}

