package com.arui.mall.core.seckill.controller;


import com.arui.mall.common.result.R;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.model.pojo.entity.SeckillProduct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-11-17
 */
@RestController
@RequestMapping("/seckill")
public class SecKillProductController {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 查询redis所有秒杀商品
     * @return
     */
    @GetMapping("queryAllSecKillProduct")
    public R<List<SeckillProduct>> queryAllSecKillProduct(){
        List<SeckillProduct> seckillProductList = redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT).values();
        return R.ok(seckillProductList);
    }

    /**
     * 秒杀商品详情
     * @param skuId
     * @return
     */
    @GetMapping("querySecKillProduct/{skuId}")
    public R<SeckillProduct> querySecKillProduct(@PathVariable Long skuId){
        SeckillProduct secKillProduct = (SeckillProduct) redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT).get(String.valueOf(skuId));
        return R.ok(secKillProduct);
    }
}

