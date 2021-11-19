package com.arui.mall.feign.client;

import com.arui.mall.common.result.R;
import com.arui.mall.model.pojo.entity.SeckillProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author ...
 */
@FeignClient(value = "mall-seckill")
public interface SecKillFeignClient {

    /**
     * 查询redis所有秒杀商品
     * @return
     */
    @GetMapping("/seckill/queryAllSecKillProduct")
    public R<List<SeckillProduct>> queryAllSecKillProduct();

    /**
     * 秒杀商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/seckill/querySecKillProduct/{skuId}")
    public R<SeckillProduct> querySecKillProduct(@PathVariable Long skuId);
}
