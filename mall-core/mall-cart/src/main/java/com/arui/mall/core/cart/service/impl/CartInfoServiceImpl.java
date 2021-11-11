package com.arui.mall.core.cart.service.impl;


import com.arui.mall.core.cart.mapper.CartInfoMapper;
import com.arui.mall.core.cart.service.AsyncCartInfoService;
import com.arui.mall.core.cart.service.CartInfoService;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.entity.CartInfo;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private AsyncCartInfoService asyncCartInfoService;

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 保存购物车信息，如果skuId和termId都有，优先保存skuId
     * @param finalUserId
     * @param skuId
     * @param skuNum
     */
    @Override
    public void addTempIdCart(String finalUserId, Long skuId, Integer skuNum) {
        // 获得购物车实体类
        CartInfo cartInfo = parseCartInfo(finalUserId, skuId, skuNum);

        // 先判断数据库中userId和skuId是否已经存在
        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("user_id", finalUserId).eq("sku_id", skuId);
        CartInfo cartInfoFromDb = baseMapper.selectOne(cartInfoQueryWrapper);
        if (cartInfoFromDb == null){
            // 直接插入DB
            asyncCartInfoService.insertCartInfoToDb(cartInfo);
            // 更新数据到redis
            // 获得redisKey [user:userID:cart] skuId cartInfo
            String cartKey = getCartKey(finalUserId);
            asyncCartInfoService.updateCartInfoRedis(cartKey,skuId.toString(), cartInfo);
        }else {
            // 更新skuNum
            Integer newSkuNum = cartInfoFromDb.getSkuNum() + skuNum;
            cartInfoFromDb.setSkuNum(newSkuNum);
            baseMapper.updateById(cartInfoFromDb);
            asyncCartInfoService.updateCartInfoRedis(getCartKey(finalUserId), skuId.toString(), cartInfoFromDb);
        }
    }

    /**
     * 展示购物车列表
     * @param finalUserId
     * @return
     */
    @Override
    public List<CartInfo> getCartList(String finalUserId) {

        // 从redis读数据cartList
//        String cartKey = getCartKey(finalUserId);
//        List<CartInfo> list = redisTemplate.opsForHash().values(cartKey);
        List<CartInfo> cartListFromDb = getCartListFromDb(finalUserId);
        return cartListFromDb;
    }

    /**
     * 从数据库获取cartList
     * @param finalUserId
     * @return
     */
    private List<CartInfo> getCartListFromDb(String finalUserId) {
        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
        cartInfoQueryWrapper.eq("user_id", finalUserId);
        List<CartInfo> cartInfoList = baseMapper.selectList(cartInfoQueryWrapper);
        return cartInfoList;
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
     * 获得redisKey [user:userID:cart]
     * @param TempOrUserId
     * @return
     */
    private String getCartKey(String TempOrUserId) {
        return RedisConstant.USER_KEY_PREFIX + TempOrUserId + RedisConstant.USER_CART_KEY_SUFFIX;
    }
}
