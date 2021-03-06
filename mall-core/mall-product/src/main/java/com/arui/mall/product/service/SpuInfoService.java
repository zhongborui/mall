package com.arui.mall.product.service;

import com.arui.mall.model.pojo.entity.SpuInfo;
import com.arui.mall.model.pojo.vo.SpuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface SpuInfoService extends IService<SpuInfo> {

    /**
     * 新增SPU
     * @param spuInfoVO
     */
    void saveSpu(SpuInfoVO spuInfoVO);
}
