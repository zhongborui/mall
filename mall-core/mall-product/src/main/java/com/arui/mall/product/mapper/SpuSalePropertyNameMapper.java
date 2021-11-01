package com.arui.mall.product.mapper;

import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * spu销售属性 Mapper 接口
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface SpuSalePropertyNameMapper extends BaseMapper<SpuSalePropertyName> {

    /**
     * 根据productId查询spu销售属性
     * @param productId
     * @return
     */
    List<SpuSalePropertyName> querySalePropertyByProductId(Long productId);

    /**
     * 根据skuId，spuId查询spu销售属性和选中的sku销售属性
     * spuSalePropertyName
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSalePropertyName> getSpuSPNAndSkuSPNSelected(Long spuId, Long skuId);
}
