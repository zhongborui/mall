package com.arui.mall.product.mapper;

import com.arui.mall.model.pojo.entity.PlatformPropertyName;
import com.arui.mall.model.pojo.vo.PlatformPropertyVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 属性表 Mapper 接口
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface PlatformPropertyNameMapper extends BaseMapper<PlatformPropertyName> {

    /**
     * 根据三级id查询商品平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<PlatformPropertyVO> getPlatformPropertyByCategoryId(Long category1Id, Long category2Id, Long category3Id);

    /**
     *
     * @param category3Id
     * @return
     */
    List<PlatformPropertyVO> getPlatformProperty(Long category3Id);
}
