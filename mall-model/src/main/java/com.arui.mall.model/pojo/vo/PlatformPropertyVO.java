package com.arui.mall.model.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 属性表
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Data

@ApiModel(value="PlatformPropertyName对象VO", description="属性表")
public class PlatformPropertyVO implements Serializable {

    @ApiModelProperty(value = "编号")
    private Long id;

    @ApiModelProperty(value = "属性名称")
    private String propertyKey;

    @ApiModelProperty(value = "分类id")
    private Long categoryId;

    @ApiModelProperty(value = "分类层级")
    private Integer categoryLevel;

    @ApiModelProperty(value = "平台属性一对多")
    private List<PlatformPropertyValueVO> platformPropertyValueVOList;
}
