package com.arui.mall.core.payment.controller;


import com.alipay.api.internal.util.AlipaySignature;
import com.arui.mall.core.payment.config.AlipayConfig;
import com.arui.mall.core.payment.service.PaymentInfoService;
import com.arui.mall.model.enums.PaymentStatus;
import com.arui.mall.model.enums.PaymentType;
import com.arui.mall.model.pojo.entity.PaymentInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 支付信息表 前端控制器
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@RestController
@RequestMapping("/payment")
public class PaymentInfoController {

    @Resource
    private PaymentInfoService paymentInfoService;

    /**
     * 保存部分订单信息，返回支付二维码
     * @param orderId
     * @return
     */
    @GetMapping("createQrCode/{orderId}")
    public String createQrCode(@PathVariable Long orderId){
        return paymentInfoService.createQrCode(orderId);
    }

    /**
     * 异步回调通知
     * @param paramMap
     * @return
     * @throws Exception
     */
    @PostMapping("/async/notify")
    public String asyncNotify(@RequestParam Map<String,String> paramMap) throws Exception {
        // 签名确认是支付宝的请求，通过支付宝发的公钥解密alipay_public_key
        boolean signVerified = AlipaySignature.rsaCheckV1(paramMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        String tradeStatus = paramMap.get("trade_status");
        String outTradeNo = paramMap.get("out_trade_no");
        // 身份校验是否成功
        if(signVerified){
            //交易成功或者完成才能更改支付订单信息
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)){
                // 获取支付信息，状态
                PaymentInfo paymentInfo = paymentInfoService.getPaymentInfo(outTradeNo, PaymentType.ALIPAY.name());
                /*
                验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，
                校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
                 */
                if (paymentInfo.getPaymentStatus().equals(PaymentStatus.ClOSED.name())||
                        paymentInfo.getPaymentStatus().equals(PaymentStatus.PAID.name())){
                    return "failure";
                }
                //修改支付订单信息（状态
                paymentInfoService.updatePaymentInfo(outTradeNo,PaymentType.ALIPAY.name(),paramMap);
                return "success";
            }
        }else{
            // 验签失败则记录异常日志，并在response中返回failure.
            return "failure";
        }
        return "failure";
    }

}

