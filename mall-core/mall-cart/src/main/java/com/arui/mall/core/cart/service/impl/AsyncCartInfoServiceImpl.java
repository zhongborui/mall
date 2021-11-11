package com.arui.mall.core.cart.service.impl;

import com.arui.mall.core.cart.mapper.CartInfoMapper;
import com.arui.mall.core.cart.service.AsyncCartInfoService;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.model.pojo.entity.CartInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 异步接口
 * @author ...
 */
@Async
@Service
public class AsyncCartInfoServiceImpl extends ServiceImpl<CartInfoMapper, CartInfo> implements AsyncCartInfoService {
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void insertCartInfoToDb(CartInfo cartInfo) {
        baseMapper.insert(cartInfo);
    }

    @Override
    public void updateCartInfoRedis(String cartKey, String skuId, CartInfo cartInfo) {
        // 设置购物车过期时间
        redisTemplate.opsForHash().put(cartKey, skuId, cartInfo);
        redisTemplate.expire(cartKey, RedisConstant.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }
}
