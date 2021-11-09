package com.arui.mall.cart.service.impl;


import com.arui.mall.cart.mapper.CartInfoMapper;
import com.arui.mall.cart.service.CartInfoService;
import com.arui.mall.model.pojo.entity.cart.CartInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 购物车表 用户登录系统时更新冗余 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-11-09
 */
@Service
public class CartInfoServiceImpl extends ServiceImpl<CartInfoMapper, CartInfo> implements CartInfoService {

}
