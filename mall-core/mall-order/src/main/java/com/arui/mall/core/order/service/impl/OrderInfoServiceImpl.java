package com.arui.mall.core.order.service.impl;

import com.arui.mall.common.util.HttpClientUtil;
import com.arui.mall.core.order.mapper.OrderInfoMapper;
import com.arui.mall.core.order.service.OrderInfoService;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.entity.OrderDetail;
import com.arui.mall.model.pojo.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClients;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单表 订单表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ProductFeignClient productFeignClient;

    /**
     * 幂等性，防止无刷新重复提交表单
     * @param userId
     * @param tradeNo
     * @return
     */
    @Override
    public boolean checkTradNo(String userId, String tradeNo) {
        // 定义key
        String tradeNoKey = "user:"+userId+":tradeNo";
        String valueInRedis = (String) redisTemplate.opsForValue().get(tradeNoKey);
        return tradeNo.equals(valueInRedis);
    }

    /**
     * 校验价格和库存
     * @param orderInfo
     * @param userId
     * @return
     */
    @Override
    public List<String> checkPriceAndStock(OrderInfo orderInfo, String userId) {
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        List<String> warningList = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetailList) {
            Long skuId = orderDetail.getSkuId();
            // 远程调用价格接口
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            int i = skuPrice.compareTo(orderDetail.getOrderPrice());
            if (i != 0){
                // 说明价格不相等,需要修改价格并提示
                warningList.add(orderDetail.getSkuName() + "价格有变化");
            }
            // 远程调用库存接口 http://localhost:8100/hasStock?skuId=28&num=1000
            String hasStorage = HttpClientUtil.doGet("http://localhost:8100/hasStock?skuId=" + skuId + "&num=" + orderDetail.getSkuNum());
            if (!hasStorage.equals("1")){
                // 没有库存了
                warningList.add(orderDetail.getSkuName() + "库存不足");
            }
        }

        return warningList;
    }

    /**
     * 以上没有问题，删除流水号
     * @param userId
     */
    @Override
    public void deleteTradNo(String userId) {
        // 定义key
        String tradeNoKey = "user:"+userId+":tradeNo";
        redisTemplate.delete(tradeNoKey);
    }
}
