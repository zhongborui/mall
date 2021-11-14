package com.arui.mall.core.order.service;

import com.arui.mall.model.enums.OrderStatus;
import com.arui.mall.model.enums.ProcessStatus;
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

    /**
     * 保存订单到数据库
     * @param orderInfo
     * @param userId
     * @return
     */
    Long saveOrderDetail(OrderInfo orderInfo, Long userId);

    /**
     * 根据订单号查看订单的状态，如果还是未支付，则将订单关闭
     * @param id
     * @return
     */
    OrderInfo checkOrderStatus(Long id);

    /**
     * 关闭订单
     * @param id
     * @param processStatus
     */
    void updateOrderStatus(Long id, ProcessStatus processStatus);
}
