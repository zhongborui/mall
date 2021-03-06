package com.arui.mall.model.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 属性值表
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Data
@ApiModel(value="PlatformPropertyValue对象VO", description="属性值表")
public class PlatformPropertyValueVO implements Serializable {


    @ApiModelProperty(value = "编号")
    private Long id;

    @ApiModelProperty(value = "属性值名称")
    private String propertyValue;

    @ApiModelProperty(value = "属性id")
    private Long propertyKeyId;

}
