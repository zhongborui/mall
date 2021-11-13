package com.arui.mall.core.order.service;

import com.arui.mall.model.pojo.entity.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 订单表 订单表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 幂等性，防止无刷新重复提交表单
     * @param userId
     * @param tradeNo
     * @return
     */
    boolean checkTradNo(String userId, String tradeNo);

    /**
     * 校验价格和库存
     * @param orderInfo
     * @param userId
     * @return
     */
    List<String> checkPriceAndStock(OrderInfo orderInfo, String userId);

    /**
     * 以上没有问题，删除流水号
     * @param userId
     */
    void deleteTradNo(String userId);
}
