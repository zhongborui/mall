package com.arui.mall.product.service;

import com.arui.mall.model.pojo.entity.SkuSalePropertyValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * sku销售属性值 服务类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface SkuSalePropertyValueService extends IService<SkuSalePropertyValue> {

    /**
     * 根据spuId查询spu销售属性组合对应的sku
     * @param spuId
     * @return
     */
    List<Map> getSpuSPVAndSkuMapping(Long spuId);
}
