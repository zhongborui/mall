package com.arui.mall.model.pojo.vo;

import com.arui.mall.model.pojo.entity.SpuImage;
import com.arui.mall.model.pojo.entity.SpuInfo;
import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SpuInfo对象VO", description="商品表")
public class SpuInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品描述(后台简述）")
    private String description;

    @ApiModelProperty(value = "三级分类id")
    private Long category3Id;

    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    @TableField(exist = false)
    private List<SpuSalePropertyName> salePropertyKeyList;

    @TableField(exist = false)
    private List<SpuImage> productImageList;


}
