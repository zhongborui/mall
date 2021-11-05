package com.arui.mall.product.service;

import com.arui.mall.model.pojo.entity.PlatformPropertyName;
import com.arui.mall.model.pojo.vo.PlatformPropertyVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 属性表 服务类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
public interface PlatformPropertyNameService extends IService<PlatformPropertyName> {

    /**
     * 根据三级id查询商品平台属性
     * @param category1ID
     * @param category2ID
     * @param category3ID
     * @return
     */
    List<PlatformPropertyVO> getPlatformPropertyByCategoryId(Long category1ID, Long category2ID, Long category3ID);

    /**
     * 新增或者修改平台属性
     * @param platformPropertyVO
     */
    void savePlatformProperty(PlatformPropertyVO platformPropertyVO);

    /**
     * 返回
     * @param category3Id
     * @return
     */
    List<PlatformPropertyVO> getPlatformProperty(Long category3Id);
}
