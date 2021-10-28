package com.arui.mall.product.service;

import com.arui.mall.model.pojo.entity.BaseCategory2;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 二级分类表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface BaseCategory2Service extends IService<BaseCategory2> {


    /**
     * 查询商品二级分类列表
     * @param category1ID
     * @return
     */
    List<BaseCategory2> getCategory2(Long category1ID);
}
