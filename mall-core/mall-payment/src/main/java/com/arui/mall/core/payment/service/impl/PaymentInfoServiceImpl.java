package com.arui.mall.core.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.arui.mall.core.payment.config.AlipayConfig;
import com.arui.mall.core.payment.mapper.PaymentInfoMapper;
import com.arui.mall.core.payment.service.PaymentInfoService;
import com.arui.mall.feign.client.OrderFeignClient;
import com.arui.mall.model.enums.PaymentStatus;
import com.arui.mall.model.enums.PaymentType;
import com.arui.mall.model.pojo.entity.OrderInfo;
import com.arui.mall.model.pojo.entity.PaymentInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 支付信息表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

    @Resource
    private OrderFeignClient orderFeignClient;

    @Resource
    private AlipayClient alipayClient;

    /**
     * 保存部分订单信息，返回支付二维码
     * @param orderId
     * @return
     */
    @SneakyThrows
    @Override
    public String createQrCode(Long orderId) {

        // 保存订单已有信息
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId);
        savePaymentInfo(orderInfo, PaymentType.ALIPAY.name());

        // 创建支付二维码(官网示例代码
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        // 同步通知
        request.setReturnUrl(AlipayConfig.return_payment_url);
        // 异步通知
        request.setNotifyUrl(AlipayConfig.notify_payment_url);
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderInfo.getOutTradeNo());
        bizContent.put("total_amount", 0.01);
        bizContent.put("subject", "iphone13");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        // 请求支付宝的返回值response
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if(response.isSuccess()){
            System.out.println("调用成功");
            return response.getBody();
        } else {
            System.out.println("调用失败");
        }

        return null;
    }

    /**
     * 获取支付信息，状态
     * @param outTradeNo
     * @param paymentType
     * @return
     */
    @Override
    public PaymentInfo getPaymentInfo(String outTradeNo, String paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no",outTradeNo);
        wrapper.eq("payment_type",paymentType);
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        return paymentInfo;
    }

    /**
     * 修改支付订单信息（状态
     * @param outTradeNo
     * @param name
     * @param paramMap
     */
    @Override
    public void updatePaymentInfo(String outTradeNo, String name, Map<String, String> paramMap) {
        PaymentInfo dbPaymentInfo = getPaymentInfo(outTradeNo, name);
        //判断查询到的数据支付状态, 关闭状态，不需要修改
        if (dbPaymentInfo.getPaymentStatus().equals(PaymentStatus.PAID.name()) ||
                dbPaymentInfo.getPaymentStatus().equals(PaymentStatus.ClOSED.name())) {
            return;
        }
        //更新支付表字段
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus(PaymentStatus.PAID.name());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(paramMap.toString());
        // 支付宝返回的订单号
        paymentInfo.setTradeNo(paramMap.get("trade_no"));
        // 更新表
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no",outTradeNo);
        baseMapper.update(paymentInfo,wrapper);
    }

    /**
     * 保存订单已有信息
     * @param orderInfo
     * @param paymentType
     */
    private void savePaymentInfo(OrderInfo orderInfo, String paymentType) {
        // 判断是否有重复订单
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("order_id",orderInfo.getId());
        paymentInfoQueryWrapper.eq("payment_type",paymentType);
        Integer count = baseMapper.selectCount(paymentInfoQueryWrapper);
        if (count>0){
            return;
        }
        //保存订单
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setOrderId(orderInfo.getId()+"");
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setPaymentMoney(orderInfo.getTotalMoney());
        paymentInfo.setPaymentContent(orderInfo.getTradeBody());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
        paymentInfo.setCreateTime(new Date());
        baseMapper.insert(paymentInfo);
    }
}
