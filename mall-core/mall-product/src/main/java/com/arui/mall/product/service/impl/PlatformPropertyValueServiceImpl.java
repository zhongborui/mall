package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.PlatformPropertyValue;
import com.arui.mall.model.pojo.vo.PlatformPropertyValueVO;
import com.arui.mall.product.mapper.PlatformPropertyValueMapper;
import com.arui.mall.product.service.PlatformPropertyValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 属性值表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class PlatformPropertyValueServiceImpl extends ServiceImpl<PlatformPropertyValueMapper, PlatformPropertyValue> implements PlatformPropertyValueService {


    /**
     * 根据平台属性key的id，查询平台属性value
     * @param propertyKeyId
     * @return
     */
    @Override
    public List<PlatformPropertyValueVO> getPropertyValueByPropertyKeyId(Long propertyKeyId) {
        QueryWrapper<PlatformPropertyValue> platformPropertyValueVOQueryWrapper = new QueryWrapper<>();
        platformPropertyValueVOQueryWrapper.eq("property_key_id", propertyKeyId);
        List<PlatformPropertyValue> platformPropertyValues = baseMapper.selectList(platformPropertyValueVOQueryWrapper);

        // 流式编程list转
        List<PlatformPropertyValueVO> list = platformPropertyValues.stream().map(platformPropertyValue -> {
            PlatformPropertyValueVO platformPropertyValueVO = new PlatformPropertyValueVO();
            BeanUtils.copyProperties(platformPropertyValue, platformPropertyValueVO);
            return platformPropertyValueVO;
        }).collect(Collectors.toList());
        return list;
    }
}
