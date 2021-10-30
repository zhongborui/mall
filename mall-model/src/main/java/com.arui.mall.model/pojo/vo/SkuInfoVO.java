package com.arui.mall.model.pojo.vo;

import com.arui.mall.model.pojo.entity.SkuImage;
import com.arui.mall.model.pojo.entity.SkuPlatformPropertyValue;
import com.arui.mall.model.pojo.entity.SkuSalePropertyValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 库存单元表
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Data

@ApiModel(value="SkuInfo对象VO", description="库存单元表")
public class SkuInfoVO implements Serializable {


    @ApiModelProperty(value = "库存id(itemID)")
    private Long id;

    @ApiModelProperty(value = "商品id")
    private Long productId;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "sku名称")
    private String skuName;

    @ApiModelProperty(value = "商品规格描述")
    private String skuDesc;

    @ApiModelProperty(value = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "品牌(冗余)")
    private Long brandId;

    @ApiModelProperty(value = "三级分类id（冗余)")
    private Long category3Id;

    @ApiModelProperty(value = "默认显示图片(冗余)")
    private String skuDefaultImg;

    @ApiModelProperty(value = "是否销售（1：是 0：否）")
    private Integer isSale;

    @ApiModelProperty(value = "sku平台属性列表")
    private List<SkuPlatformPropertyValue> skuPlatformPropertyValueList;

    @ApiModelProperty(value = "sku销售属性列表")
    private List<SkuSalePropertyValue> skuSalePropertyValueList;

    @ApiModelProperty(value = "sku照片列表")
    private List<SkuImage> skuImageList;
}
