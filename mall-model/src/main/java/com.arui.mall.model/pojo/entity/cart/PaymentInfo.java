package com.arui.mall.model.pojo.entity.cart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 支付信息表
 * </p>
 *
 * @author ...
 * @since 2021-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PaymentInfo对象", description="支付信息表")
public class PaymentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "对外业务编号")
    private String outTradeNo;

    @ApiModelProperty(value = "订单编号")
    private String orderId;

    @ApiModelProperty(value = "支付类型（微信 支付宝）")
    private String paymentType;

    @ApiModelProperty(value = "交易编号")
    private String tradeNo;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal paymentMoney;

    @ApiModelProperty(value = "交易内容")
    private String paymentContent;

    @ApiModelProperty(value = "支付状态")
    private String paymentStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "回调时间")
    private Date callbackTime;

    @ApiModelProperty(value = "回调信息")
    private String callbackContent;


}
