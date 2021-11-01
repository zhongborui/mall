package com.arui.mall.product.service;

import com.arui.mall.model.pojo.entity.SkuInfo;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

/**
 * <p>
 * 库存单元表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * 新增sku
     * @param skuInfoVO
     */
    void saveSkuInfo(SkuInfoVO skuInfoVO);

    /**
     * getSkuDetailById
     * @param skuId
     * @return
     */
    SkuInfoVO getSkuDetailById(Long skuId);

    /**
     * getSkuPrice
     * @param skuId
     * @return
     */
    BigDecimal getSkuPrice(Long skuId);
}
