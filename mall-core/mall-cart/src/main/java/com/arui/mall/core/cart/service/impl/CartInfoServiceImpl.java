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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * @param userId
     * @param userTempId
     * @return
     */
    @Override
    public List<CartInfo> getCartList(String userId, String userTempId) {
        // 有临时id
        if (!StringUtils.isEmpty(userTempId)){
            // 先将tempId购物车列表查出来
            List<CartInfo> cartListFromDbByUserTempId = getCartListFromDb(userTempId);
            // 有userId，说明登录了，需要合并购物车
            if (!StringUtils.isEmpty(userId)){
                // 根据userId, 的购物车列表
                List<CartInfo> cartListFromDb = getCartListFromDb(userId);
                // skuId相同的skuNUm相加，否则，修改userId即可
                // 将list转为map，这是登录用户的购物车
                Map<Long, CartInfo> cartInfoMap = cartListFromDb.stream().collect(Collectors.toMap(CartInfo::getSkuId, cartInfo -> cartInfo));
                // 需要删除的购物车
                List<CartInfo> cartInfoDelete = new ArrayList<>();
                for (CartInfo cartInfoTemp : cartListFromDbByUserTempId) {
                    // 说明userId相等，skuNum相加
                    if (cartInfoMap.containsKey(cartInfoTemp.getSkuId())){
                        // 已登录的购物车
                        CartInfo cartInfo1 = cartInfoMap.get(cartInfoTemp.getSkuId());
                        cartInfo1.setSkuNum(cartInfo1.getSkuNum() + cartInfoTemp.getSkuNum());
                        baseMapper.updateById(cartInfo1);
                        cartInfoDelete.add(cartInfoTemp);
                        QueryWrapper<CartInfo> cartInfoQueryWrapper = new QueryWrapper<>();
                        cartInfoQueryWrapper.eq("user_id", userTempId);
                        baseMapper.delete(cartInfoQueryWrapper);
                    }else {
                        // 不相等，修改临时用户购物车的userId
                        cartInfoTemp.setUserId(userId);
                        baseMapper.updateById(cartInfoTemp);
                    }
                }
                // 返回合并购物车
                return getCartListFromDb(userId);
            }
        }else{
            if (!StringUtils.isEmpty(userId)){
                // 只返回登录购物车
                return getCartListFromDb(userId);
            }

        }
        // 从redis读数据cartList
//        String cartKey = getCartKey(finalUserId);
//        List<CartInfo> list = redisTemplate.opsForHash().values(cartKey);

        // 只返回临时购物车
        return getCartListFromDb(userTempId);
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
