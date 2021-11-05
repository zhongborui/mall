package com.arui.mall.product.controller.api;


import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.entity.BaseBrand;
import com.arui.mall.model.pojo.entity.SkuInfo;
import com.arui.mall.model.pojo.vo.PlatformPropertyVO;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.arui.mall.product.service.BaseBrandService;
import com.arui.mall.product.service.PlatformPropertyNameService;
import com.arui.mall.product.service.SkuInfoService;
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
 * 库存单元表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Api(tags = "后台管理商品sku")
@RestController
@RequestMapping("/product")
public class ApiSkuController {

    @Resource
    private PlatformPropertyNameService platformPropertyNameService;

    @Resource
    private BaseBrandService baseBrandService;

    /**
     * 返回平台属性信息
     * @param category3Id
     * @return
     */
    @GetMapping("getPlatformProperty/{category3Id}")
    public List<PlatformPropertyVO> getPlatformProperty(@PathVariable Long category3Id){
        List<PlatformPropertyVO> platformPropertyVOList = platformPropertyNameService.getPlatformProperty(category3Id);
        return platformPropertyVOList;
    }

    /**
     * 返回品牌信息
     * @param
     * @return
     */
    @GetMapping("getBrandInfo/{brandId}")
    public BaseBrand getBrandInfo(@PathVariable Long brandId){
        BaseBrand baseBrand = baseBrandService.getById(brandId);
        return baseBrand;
    }
}

