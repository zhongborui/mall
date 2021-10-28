package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.BaseCategory3;
import com.arui.mall.product.mapper.BaseCategory3Mapper;
import com.arui.mall.product.service.BaseCategory3Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 三级分类表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3> implements BaseCategory3Service {

    /**
     * @param category2ID
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2ID) {
        QueryWrapper<BaseCategory3> baseCategory2QueryWrapper = new QueryWrapper<>();
        baseCategory2QueryWrapper.eq("category2_id", category2ID);
        List<BaseCategory3> baseCategory3List = baseMapper.selectList(baseCategory2QueryWrapper);
        return baseCategory3List;
    }
}
