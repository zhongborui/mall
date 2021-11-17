package com.arui.mall.model.pojo.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author ...
 * @since 2021-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SeckillProduct对象", description="")
public class SeckillProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "product_id")
    private Long spuId;

    @ApiModelProperty(value = "sku_id")
    private Long skuId;

    @ApiModelProperty(value = "标题")
    private String skuName;

    @ApiModelProperty(value = "商品图片")
    private String skuDefaultImg;

    @ApiModelProperty(value = "原价格")
    private BigDecimal price;

    @ApiModelProperty(value = "秒杀价格")
    private BigDecimal costPrice;

    @ApiModelProperty(value = "添加日期")
    private Date createTime;

    @ApiModelProperty(value = "审核日期")
    private Date checkTime;

    @ApiModelProperty(value = "审核状态 1为秒杀商品 2为结束 3审核未通过")
    private String status;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "秒杀商品数")
    private Integer num;

    @ApiModelProperty(value = "剩余库存数")
    private Integer stockCount;

    @ApiModelProperty(value = "描述")
    private String skuDesc;


}
