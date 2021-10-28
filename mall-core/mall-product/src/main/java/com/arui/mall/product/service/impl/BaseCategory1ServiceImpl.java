package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.BaseCategory1;
import com.arui.mall.product.mapper.BaseCategory1Mapper;
import com.arui.mall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 一级分类表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class BaseCategory1ServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategory1Service {

    /**
     * 查询商品一级分类列表
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseMapper.selectList(null);
    }
}
