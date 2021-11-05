package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.PlatformPropertyName;
import com.arui.mall.model.pojo.entity.PlatformPropertyValue;
import com.arui.mall.model.pojo.vo.PlatformPropertyVO;
import com.arui.mall.model.pojo.vo.PlatformPropertyValueVO;
import com.arui.mall.product.mapper.PlatformPropertyNameMapper;
import com.arui.mall.product.service.PlatformPropertyNameService;
import com.arui.mall.product.service.PlatformPropertyValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 属性表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class PlatformPropertyNameServiceImpl extends ServiceImpl<PlatformPropertyNameMapper, PlatformPropertyName> implements PlatformPropertyNameService {

    @Resource
    private PlatformPropertyValueService platformPropertyValueService;

    /**
     * 根据三级id查询商品平台属性
     * @param category1ID
     * @param category2ID
     * @param category3ID
     * @return
     */
    @Override
    public List<PlatformPropertyVO> getPlatformPropertyByCategoryId(Long category1ID, Long category2ID, Long category3ID) {
        return baseMapper.getPlatformPropertyByCategoryId(category1ID, category2ID, category3ID);
    }

    /**
     * 新增或者修改平台属性
     * @param platformPropertyVO
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void savePlatformProperty(PlatformPropertyVO platformPropertyVO) {
        PlatformPropertyName platformPropertyName = new PlatformPropertyName();
        BeanUtils.copyProperties(platformPropertyVO, platformPropertyName);

        // 先判断库中是否已经有数据，有-修改
        QueryWrapper<PlatformPropertyName> platformPropertyNameQueryWrapper = new QueryWrapper<>();
        platformPropertyNameQueryWrapper.eq("property_key", platformPropertyName.getPropertyKey());
        PlatformPropertyName platformPropertyName1 = baseMapper.selectOne(platformPropertyNameQueryWrapper);

        Long id = null;

        if (platformPropertyName1 == null){
            // 插入
            // 插入name 返回主键
            baseMapper.insert(platformPropertyName);
            id = platformPropertyName.getId();
        }else {
            // 更新
            baseMapper.updateById(platformPropertyName);
            id = platformPropertyName1.getId();
        }


            // 删除平台属性value的值
            QueryWrapper<PlatformPropertyValue> platformPropertyValueQueryWrapper = new QueryWrapper<>();
            platformPropertyValueQueryWrapper.eq("property_key_id", id);
            platformPropertyValueService.remove(platformPropertyValueQueryWrapper);

            final Long pid = id;
        // 流处理数据
        List<PlatformPropertyValueVO> platformPropertyValueListVO = platformPropertyVO.getPropertyValueList();
        List<PlatformPropertyValue> platformPropertyValues = platformPropertyValueListVO.stream().map(platformPropertyValueVO -> {

            PlatformPropertyValue platformPropertyValue = new PlatformPropertyValue();
            BeanUtils.copyProperties(platformPropertyValueVO, platformPropertyValue);
            platformPropertyValue.setPropertyKeyId(pid);
            return platformPropertyValue;
        }).collect(Collectors.toList());

        // 批量插入数据
        platformPropertyValueService.saveBatch(platformPropertyValues);
    }

    @Override
    public List<PlatformPropertyVO> getPlatformProperty(Long category3Id) {
        List<PlatformPropertyVO> platformPropertyVOList = baseMapper.getPlatformProperty(category3Id);
        return platformPropertyVOList;
    }
}
