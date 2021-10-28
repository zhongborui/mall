package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.BaseCategory2;
import com.arui.mall.product.mapper.BaseCategory2Mapper;
import com.arui.mall.product.service.BaseCategory2Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 二级分类表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2> implements BaseCategory2Service {

    /**
     * 查询商品二级分类列表
     * @param category1ID
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1ID) {
        QueryWrapper<BaseCategory2> baseCategory2QueryWrapper = new QueryWrapper<>();
        baseCategory2QueryWrapper.eq("category1_id", category1ID);
        List<BaseCategory2> baseCategory2List = baseMapper.selectList(baseCategory2QueryWrapper);
        return baseCategory2List;
    }
}
