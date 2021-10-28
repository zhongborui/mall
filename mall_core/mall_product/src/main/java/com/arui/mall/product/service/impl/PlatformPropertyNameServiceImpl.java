package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.PlatformPropertyName;
import com.arui.mall.model.pojo.vo.PlatformPropertyVO;
import com.arui.mall.product.mapper.PlatformPropertyNameMapper;
import com.arui.mall.product.service.PlatformPropertyNameService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
}
