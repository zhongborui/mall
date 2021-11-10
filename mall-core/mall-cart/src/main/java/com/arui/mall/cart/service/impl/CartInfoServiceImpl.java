package com.arui.mall.cart.service.impl;


import com.arui.mall.cart.mapper.CartInfoMapper;
import com.arui.mall.cart.service.CartInfoService;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.entity.cart.CartInfo;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 保存临时id购物车信息
     * @param userTempId
     * @param skuId
     * @param skuNum
     */
    @Override
    public void addTempIdCart(String userTempId, Long skuId, Integer skuNum) {

        // 获得购物车实体类
        CartInfo cartInfo = parseCartInfo(userTempId, skuId, skuNum);

        // 先判断数据库中userId和skuId是否已经存在
        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("user_id", userTempId).eq("sku_id", skuId);
        CartInfo cartInfoFromDb = baseMapper.selectOne(cartInfoQueryWrapper);
        if (cartInfoFromDb == null){
            // 直接插入DB
            insertCartInfoToDb(cartInfo);
            // 更新数据到redis
            // 获得redisKey [user:userID:cart] skuId cartInfo
            String cartKey = getCartKey(userTempId);
            updateCartInfoRedis(cartKey,skuId.toString(), cartInfo);
        }else {
            // 更新skuNum
            Integer newSkuNum = cartInfoFromDb.getSkuNum() + skuNum;
            cartInfoFromDb.setSkuNum(newSkuNum);
            baseMapper.updateById(cartInfoFromDb);
            updateCartInfoRedis(getCartKey(userTempId), skuId.toString(), cartInfoFromDb);
        }


    }

    /**
     * 更新数据到redis
     * @param skuId
     * @param cartInfo
     * @param cartKey
     */
    private void updateCartInfoRedis(String cartKey, String skuId, CartInfo cartInfo) {
        redisTemplate.opsForHash().put(cartKey, skuId, cartInfo);
    }

    /**
     * 获得redisKey [user:userID:cart]
     * @param TempOrUserId
     * @return
     */
    private String getCartKey(String TempOrUserId) {
        return RedisConstant.USER_KEY_PREFIX + TempOrUserId + RedisConstant.USER_CART_KEY_SUFFIX;
    }

    /**
     * 获得购物车实体类
     * @param userTempId
     * @param skuId
     * @param skuNum
     * @return
     */
    private CartInfo parseCartInfo(String userTempId, Long skuId, Integer skuNum) {
        SkuInfoVO skuDetailById = productFeignClient.getSkuDetailById(skuId);
        CartInfo cartInfo = new CartInfo();
        cartInfo.setCartPrice(productFeignClient.getSkuPrice(skuId));
        cartInfo.setSkuNum(skuNum);
        cartInfo.setSkuId(skuId);
        cartInfo.setUserId(userTempId);
        cartInfo.setImgUrl(skuDetailById.getSkuDefaultImg());
        cartInfo.setSkuName(skuDetailById.getSkuName());
        return cartInfo;
    }

    /**
     * 插入临时购物车信息进入到数据库
     * @param cartInfo
     */
    private void insertCartInfoToDb(CartInfo cartInfo) {
        baseMapper.insert(cartInfo);
    }
}
