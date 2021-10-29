package com.arui.mall.product.service;

import com.arui.mall.model.pojo.entity.PlatformPropertyValue;
import com.arui.mall.model.pojo.vo.PlatformPropertyValueVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 属性值表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface PlatformPropertyValueService extends IService<PlatformPropertyValue> {

    /**
     * 根据平台属性key的id，查询平台属性value
     * @param propertyKeyId
     * @return
     */
    List<PlatformPropertyValueVO> getPropertyValueByPropertyKeyId(Long propertyKeyId);
}
