package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.SkuImage;
import com.arui.mall.model.pojo.entity.SkuInfo;
import com.arui.mall.model.pojo.entity.SkuPlatformPropertyValue;
import com.arui.mall.model.pojo.entity.SkuSalePropertyValue;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.arui.mall.product.mapper.SkuInfoMapper;
import com.arui.mall.product.service.SkuImageService;
import com.arui.mall.product.service.SkuInfoService;
import com.arui.mall.product.service.SkuPlatformPropertyValueService;
import com.arui.mall.product.service.SkuSalePropertyValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jdk.nashorn.internal.ir.CallNode;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 库存单元表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {

    @Resource
    private SkuInfoService skuInfoService;

    @Resource
    private SkuPlatformPropertyValueService skuPlatformPropertyValueService;

    @Resource
    private SkuSalePropertyValueService skuSalePropertyValueService;

    @Resource
    private SkuImageService skuImageService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSkuInfo(SkuInfoVO skuInfoVO) {
        // 插入sku_info表
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVO, skuInfo);
        Long productId = skuInfoVO.getSpuId();
        skuInfo.setSpuId(productId);
        baseMapper.insert(skuInfo);

        // 返回sku主键
        Long id = skuInfo.getId();

        // 插入sku_platform_property_value表
        List<SkuPlatformPropertyValue> skuPlatformPropertyValueList = skuInfoVO.getSkuPlatformPropertyValueList();
        for (SkuPlatformPropertyValue skuPlatformPropertyValue : skuPlatformPropertyValueList) {
            skuPlatformPropertyValue.setSkuId(id);
        }
        skuPlatformPropertyValueService.saveBatch(skuPlatformPropertyValueList);

        // 插入sku_sale_property_value
        List<SkuSalePropertyValue> skuSalePropertyValueList = skuInfoVO.getSkuSalePropertyValueList();
        for (SkuSalePropertyValue skuSalePropertyValue : skuSalePropertyValueList) {
            skuSalePropertyValue.setSkuId(id);
            skuSalePropertyValue.setProductId(productId);
        }
        skuSalePropertyValueService.saveBatch(skuSalePropertyValueList);

        // 插入sku_image表
        List<SkuImage> skuImageList = skuInfoVO.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(id);
        }
        skuImageService.saveBatch(skuImageList);
    }

    @Override
    public SkuInfoVO getSkuDetailById(Long skuId) {
        SkuInfoVO skuInfoVO = new SkuInfoVO();

        SkuInfo skuInfo = baseMapper.selectById(skuId);
        if (skuInfoVO != null){
            BeanUtils.copyProperties(skuInfo, skuInfoVO);

            QueryWrapper<SkuImage> skuImageQueryWrapper = new QueryWrapper<>();
            skuImageQueryWrapper.eq("sku_id", skuId);
            List<SkuImage> list = skuImageService.list(skuImageQueryWrapper);
            skuInfoVO.setSkuImageList(list);
        }

        return skuInfoVO;
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {

        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.select("price").eq("id", skuId);
        BigDecimal price = (BigDecimal) skuInfoService.getObj(skuInfoQueryWrapper);
        return price;
    }
}
