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

/**
 * <p>
 * 订单明细表
 * </p>
 *
 * @author ...
 * @since 2021-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderDetail对象", description="订单明细表")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "订单编号")
    private Long orderId;

    @ApiModelProperty(value = "sku_id")
    private Long skuId;

    @ApiModelProperty(value = "sku名称（冗余)")
    private String skuName;

    @ApiModelProperty(value = "图片名称（冗余)")
    private String imgUrl;

    @ApiModelProperty(value = "购买价格(下单时sku价格）")
    private BigDecimal orderPrice;

    @ApiModelProperty(value = "购买个数")
    private String skuNum;


}
