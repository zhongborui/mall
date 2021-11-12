package com.arui.mall.core.cart.service;

import com.arui.mall.model.pojo.entity.CartInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 购物车表 用户登录系统时更新冗余 服务类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
public interface CartInfoService extends IService<CartInfo> {

    /**
     * 保存购物车信息，如果skuId和termId都有，优先保存skuId
     * @param finalUserId
     * @param skuId
     * @param skuNum
     */
    void addTempIdCart(String finalUserId, Long skuId, Integer skuNum);

    /**
     * 展示购物车列表
     * @param userId
     * @param userTempId
     * @return
     */
    List<CartInfo> getCartList(String userId, String userTempId);
}
