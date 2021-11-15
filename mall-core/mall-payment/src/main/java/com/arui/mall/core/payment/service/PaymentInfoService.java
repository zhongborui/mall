package com.arui.mall.core.payment.service;

import com.arui.mall.model.pojo.entity.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 支付信息表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
public interface PaymentInfoService extends IService<PaymentInfo> {

    /**
     * 保存部分订单信息，返回支付二维码
     * @param orderId
     * @return
     */
    String createQrCode(Long orderId);

    /**
     * 获取支付信息，状态
     * @param outTradeNo
     * @param name
     * @return
     */
    PaymentInfo getPaymentInfo(String outTradeNo, String name);

    /**
     * 修改支付订单信息（状态
     * @param outTradeNo
     * @param name
     * @param paramMap
     */
    void updatePaymentInfo(String outTradeNo, String name, Map<String, String> paramMap);
}
