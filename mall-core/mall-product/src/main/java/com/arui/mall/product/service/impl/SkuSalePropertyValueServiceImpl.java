package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.SkuSalePropertyValue;
import com.arui.mall.product.mapper.SkuSalePropertyValueMapper;
import com.arui.mall.product.service.SkuSalePropertyValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * sku销售属性值 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class SkuSalePropertyValueServiceImpl extends ServiceImpl<SkuSalePropertyValueMapper, SkuSalePropertyValue> implements SkuSalePropertyValueService {


    /**
     * 根据spuId查询spu销售属性组合对应的sku
     * @param spuId
     * @return
     */
    @Override
    public List<Map> getSpuSPVAndSkuMapping(Long spuId) {
        List<Map> map = baseMapper.getSpuSPVAndSkuMapping(spuId);
        return map;
    }
}
