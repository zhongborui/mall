package com.arui.mall.core.seckill.service.impl;

import com.arui.mall.common.result.R;
import com.arui.mall.common.result.RetValCodeEnum;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.core.seckill.mapper.SeckillProductMapper;
import com.arui.mall.core.seckill.service.SeckillProductService;
import com.arui.mall.model.pojo.entity.PrepareSeckillOrder;
import com.arui.mall.model.pojo.entity.SeckillProduct;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-11-17
 */
@Service
public class SeckillProductServiceImpl extends ServiceImpl<SeckillProductMapper, SeckillProduct> implements SeckillProductService {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 更新库存
     * @param skuId
     */
    @Override
    public void updateSecKillStockCount(String skuId) {
        //锁定库存量=总的数量-剩余的队列个数
        Long leftCount = redisTemplate.boundListOps(RedisConstant.SECKILL_STOCK_PREFIX + skuId).size();
        //设置更新频率
        if(leftCount%2==0){
            SeckillProduct seckillProduct = (SeckillProduct) redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT).get(String.valueOf(skuId));
            Integer totalNum = seckillProduct.getNum();
            int leftCountInt = Integer.parseInt(leftCount + "");
            int stockCount=totalNum-leftCountInt;
            seckillProduct.setStockCount(stockCount);
            // 更新数据库
            baseMapper.updateById(seckillProduct);
            //页面显示剩余库存
            redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT).put(skuId,seckillProduct);
        }
    }

    /**
     * 判断是否有下单资格
     * @param skuId
     * @param userId
     * @return
     */
    @Override
    public R hasQualified(Long skuId, String userId) {
        // redis预下单是否有

        //如果预下单中有
        PrepareSeckillOrder prepareSeckillOrder = (PrepareSeckillOrder) redisTemplate.boundHashOps(RedisConstant.PREPARE_SECKILL_USERID_ORDER).get(userId);
        if (prepareSeckillOrder!=null){
            return R.data(prepareSeckillOrder, RetValCodeEnum.PREPARE_SECKILL_SUCCESS);
        }
        //判断是否购买了重复商品
        String orderId = (String) redisTemplate.boundHashOps(RedisConstant.BOUGHT_SECKILL_USER_ORDER).get(userId);
        if (!StringUtils.isEmpty(orderId)){
            return R.data(orderId, RetValCodeEnum.SECKILL_ORDER_SUCCESS);
        }
        //其他情况就是排队中
        return R.data(null, RetValCodeEnum.SECKILL_RUN);
    }
}
