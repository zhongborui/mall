package com.arui.mall.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息枚举
 * @author ...
 */
@Getter
public enum RetValCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),

    PAY_RUN(205, "支付中"),

    NO_LOGIN(208, "未登陆"),
    NO_PERMISSION(209, "没有权限"),
    SECKILL_NO_START(210, "秒杀还没开始"),
    SECKILL_RUN(211, "正在排队中"),
    SECKILL_NO_PAY_ORDER(212, "您有未支付的订单"),
    SECKILL_FINISH(213, "已售罄"),
    SECKILL_END(214, "秒杀已结束"),
    PREPARE_SECKILL_SUCCESS(215, "抢单成功"),
    SECKILL_FAIL(216, "抢单失败"),
    SECKILL_ILLEGAL(217, "请求不合法"),
    SECKILL_ORDER_SUCCESS(218, "下单成功"),
    ;

    private Integer code;

    private String message;

    private RetValCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
