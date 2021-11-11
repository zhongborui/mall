package com.arui.mall.core.cart.service;

import com.arui.mall.model.pojo.entity.CartInfo;

/**
 * @author ...
 */
public interface AsyncCartInfoService {
    /**
     * 插入购物车信息进入到数据库
     * @param cartInfo
     */
    void insertCartInfoToDb(CartInfo cartInfo);

    /**
     * 更新购物车信息redis
     * @param cartKey
     * @param skuId
     * @param cartInfo
     */
    void updateCartInfoRedis(String cartKey, String skuId, CartInfo cartInfo);
}
