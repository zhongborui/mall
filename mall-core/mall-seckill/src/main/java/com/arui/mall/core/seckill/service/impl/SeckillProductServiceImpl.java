package com.arui.mall.core.seckill.service.impl;

import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.core.seckill.mapper.SeckillProductMapper;
import com.arui.mall.core.seckill.service.SeckillProductService;
import com.arui.mall.model.pojo.entity.SeckillProduct;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
}
