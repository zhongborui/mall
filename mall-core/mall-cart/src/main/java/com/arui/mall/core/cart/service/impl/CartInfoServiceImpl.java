package com.arui.mall.core.cart.service.impl;


import com.arui.mall.core.cart.mapper.CartInfoMapper;
import com.arui.mall.core.cart.service.CartInfoService;
import com.arui.mall.model.pojo.entity.CartInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车表 用户登录系统时更新冗余 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@Service
public class CartInfoServiceImpl extends ServiceImpl<CartInfoMapper, CartInfo> implements CartInfoService {

}
