package com.arui.mall.core.seckill.service;

import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.entity.SeckillProduct;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ...
 * @since 2021-11-17
 */
public interface SeckillProductService extends IService<SeckillProduct> {

    /**
     * 更新库存
     * @param skuId
     */
    void updateSecKillStockCount(String skuId);

    /**
     * 判断是否有下单资格
     * @param skuId
     * @param userId
     * @return
     */
    R hasQualified(Long skuId, String userId);
}
