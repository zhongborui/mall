package com.arui.mall.cart.service;

import com.arui.mall.model.pojo.entity.cart.CartInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 购物车表 用户登录系统时更新冗余 服务类
 * </p>
 *
 * @author ...
 * @since 2021-11-09
 */
public interface CartInfoService extends IService<CartInfo> {

    /**
     * 保存临时id购物车信息
     * @param userTempId
     * @param skuId
     * @param skuNum
     */
    void addTempIdCart(String userTempId, Long skuId, Integer skuNum);
}
