package com.arui.mall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.model.pojo.entity.BaseCategoryView;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * VIEW 服务类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface BaseCategoryViewService extends IService<BaseCategoryView> {

    /**
     * 查询首页三级分类信息
     * @return
     */
    List<JSONObject> getBaseCategoryList();
}
