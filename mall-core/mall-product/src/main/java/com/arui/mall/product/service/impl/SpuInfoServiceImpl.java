package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.*;
import com.arui.mall.model.pojo.vo.SpuInfoVO;
import com.arui.mall.product.mapper.SpuInfoMapper;
import com.arui.mall.product.service.SpuImageService;
import com.arui.mall.product.service.SpuInfoService;
import com.arui.mall.product.service.SpuSalePropertyNameService;
import com.arui.mall.product.service.SpuSalePropertyValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo> implements SpuInfoService {

    @Resource
    private SpuImageService spuImageService;

    @Resource
    private SpuSalePropertyNameService spuSalePropertyNameService;

    @Resource
    private SpuSalePropertyValueService spuSalePropertyValueService;

    /**
     * 新增SPU
     * @param spuInfoVO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpu(SpuInfoVO spuInfoVO) {
        SpuInfo spuInfo = new SpuInfo();
        BeanUtils.copyProperties(spuInfoVO, spuInfo);
        baseMapper.insert(spuInfo);

        // 返回spu-id
        Long id = spuInfo.getId();

        // 插入图片
        List<SpuImage> productImageList = spuInfoVO.getProductImageList();
        for (SpuImage spuImage : productImageList) {
            spuImage.setSpuId(id);
        }
        spuImageService.saveBatch(productImageList);

        // 插入spu销售属性名称

        List<SpuSalePropertyName> salePropertyKeyList = spuInfoVO.getSalePropertyKeyList();
        for (SpuSalePropertyName spuSalePropertyName : salePropertyKeyList) {
            spuSalePropertyName.setSpuId(id);
            List<SpuSalePropertyValue> salePropertyValueList = spuSalePropertyName.getSalePropertyValueList();
            for (SpuSalePropertyValue spuSalePropertyValue : salePropertyValueList) {
                spuSalePropertyValue.setSpuId(id);
                spuSalePropertyValue.setSalePropertyKeyName(spuSalePropertyName.getSalePropertyKeyName());
            }
            spuSalePropertyValueService.saveBatch(salePropertyValueList);

        }
        spuSalePropertyNameService.saveBatch(salePropertyKeyList);

        // 插入spu销售属性value


    }
}
