package com.arui.mall.product.service;

import com.arui.mall.model.pojo.entity.BaseCategory1;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 一级分类表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface BaseCategory1Service extends IService<BaseCategory1> {

    /**
     * 查询商品一级分类列表
     * @return
     */
    List<BaseCategory1> getCategory1();

}
