package com.arui.mall.product.service;

import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * spu销售属性 服务类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface SpuSalePropertyNameService extends IService<SpuSalePropertyName> {

    /**
     * 根据productId查询spu销售属性
     * @param productId
     * @return
     */
    List<SpuSalePropertyName> querySalePropertyByProductId(Long productId);
}
