package com.arui.mall.core.seckill.controller;


import com.arui.mall.common.result.R;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.model.pojo.entity.SeckillProduct;
import io.swagger.annotations.Api;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-11-17
 */
@Api(tags = "秒杀接口")
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

    /**
     * http://api.gmall.com/seckill/generateSeckillCode/24
     * 生成下单码
     * 下单码是Md5加密userId
     * @param skuId
     * @param request
     * @return
     */
    @GetMapping("generateSeckillCode/{skuId}")
    public R generateSeckillCode(@PathVariable Long skuId, HttpServletRequest request){
        String userId = request.getHeader("userId");
        if (!StringUtils.isEmpty(userId)) {
            SeckillProduct secKillProduct = (SeckillProduct) redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT).get(String.valueOf(skuId));
            // 判断改秒杀商品是否符合规则
            if (secKillProduct != null) {
                LocalDateTime now = LocalDateTime.now();
                // 在秒杀时间范围内，可以进行秒杀

                // userId md5加密生成下单码
                String seckillCode = DigestUtils.md5DigestAsHex(userId.getBytes());
                return R.ok(seckillCode);
            }
        }
        return R.error().message("获取下单码失败");
    }

}

