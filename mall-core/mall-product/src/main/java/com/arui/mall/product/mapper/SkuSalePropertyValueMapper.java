package com.arui.mall.product.mapper;

import com.arui.mall.model.pojo.entity.SkuSalePropertyValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * sku销售属性值 Mapper 接口
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface SkuSalePropertyValueMapper extends BaseMapper<SkuSalePropertyValue> {

    /**
     * getSpuSPVAndSkuMapping
     * @param spuId
     * @return
     */
    List<Map> getSpuSPVAndSkuMapping(Long spuId);
}
