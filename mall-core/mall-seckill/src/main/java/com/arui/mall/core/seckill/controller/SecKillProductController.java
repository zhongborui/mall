package com.arui.mall.core.seckill.controller;


import com.arui.mall.common.result.R;
import com.arui.mall.common.result.RetValCodeEnum;
import com.arui.mall.common.util.AuthContextHolder;
import com.arui.mall.core.constant.MqConst;
import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.core.seckill.service.SeckillProductService;
import com.arui.mall.feign.client.UserFeignClient;
import com.arui.mall.model.pojo.entity.OrderDetail;
import com.arui.mall.model.pojo.entity.PrepareSeckillOrder;
import com.arui.mall.model.pojo.entity.SeckillProduct;
import com.arui.mall.model.pojo.entity.UserAddress;
import io.swagger.annotations.Api;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private SeckillProductService seckillProductService;

    @Resource
    private UserFeignClient userFeignClient;

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

    /**
     * http://api.gmall.com/seckill/prepareSeckill/24?seckillCode=eccbc87e4b5ce2fe28308fd9f2a7baf3
     * 预下单
     * @param skuId
     * @param seckillCode
     * @return
     */
    @PostMapping("prepareSeckill/{skuId}")
    public R prepareSeckill(@PathVariable Long skuId, String seckillCode, HttpServletRequest request){
        String userId = request.getHeader("userId");
        // 判断抢购码是否正确
        String digestCode = DigestUtils.md5DigestAsHex(userId.getBytes());
        if (!digestCode.equals(seckillCode)){
            // 非法
            return R.data(null, RetValCodeEnum.SECKILL_ILLEGAL);
        }

        // 判断状态位
        SeckillProduct redisProduct = (SeckillProduct) redisTemplate.boundHashOps(RedisConstant.SECKILL_PRODUCT).get(String.valueOf(skuId));
        if (StringUtils.isEmpty(redisProduct.getStatus())){
            return R.data(null, RetValCodeEnum.SECKILL_ILLEGAL);
        }

        if (RedisConstant.CAN_SECKILL.equals(redisProduct.getStatus())){
            // 可以秒杀， mq发送用户id和商品skuid
           Map<String, String> retMap = new HashMap<>();
           retMap.put("userId", userId);
           retMap.put("skuId", String.valueOf(skuId));
           rabbitTemplate.convertAndSend(MqConst.PREPARE_SECKILL_EXCHANGE, MqConst.PREPARE_SECKILL_ROUTE_KEY, retMap);
        }else {
            // 秒杀结束
            return R.data(null, RetValCodeEnum.SECKILL_END);
        }

        return R.ok();
    }

    /**
     * http://api.gmall.com/seckill/hasQualified/24
     * 判断订单是否有下单资格
     * @param skuId
     * @param request
     * @return
     */
    @GetMapping("hasQualified/{skuId}")
    public R hasQualified(@PathVariable Long skuId, HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        return seckillProductService.hasQualified(skuId, userId);
    }

    /**
     * 秒杀确认消息
     * @param request
     * @return
     */
    @GetMapping("/seckillConfirm")
    public R seckillConfirm(HttpServletRequest request){
        String userId = AuthContextHolder.getUserId(request);
        //秒杀到的预售商品订单信息
        PrepareSeckillOrder prepareSeckillOrder = (PrepareSeckillOrder) redisTemplate.boundHashOps(RedisConstant.PREPARE_SECKILL_USERID_ORDER).get(userId);
        if (prepareSeckillOrder ==null){
            return R.error().message("非法操作");
        }
        //获取用户收货地址列表
        List<UserAddress> userAddressList = userFeignClient.getUserAddressList(Long.parseLong(userId));
        //商品
        SeckillProduct seckillProduct = prepareSeckillOrder.getSeckillProduct();
        //秒杀实体类
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setSkuId(seckillProduct.getSkuId());
        orderDetail.setSkuName(seckillProduct.getSkuName());
        orderDetail.setImgUrl(seckillProduct.getSkuDefaultImg());
        orderDetail.setSkuNum(prepareSeckillOrder.getBuyNum()+"");
        orderDetail.setOrderPrice(seckillProduct.getCostPrice());
        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderDetailList.add(orderDetail);

        //把这些数据封装到一个map中
        Map<String, Object> result = new HashMap<>();
        result.put("userAddressList",userAddressList);
        result.put("orderDetailList",orderDetailList);
        result.put("totalMoney",seckillProduct.getCostPrice());
        return R.ok(result);
    }

}

