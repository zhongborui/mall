package com.arui.mall.product.service;

import com.arui.mall.model.pojo.entity.BaseCategory3;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 三级分类表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface BaseCategory3Service extends IService<BaseCategory3> {

    /**
     * 查询商品三级分类列表
     * @param category2ID
     * @return
     */
    List<BaseCategory3> getCategory3(Long category2ID);
}
