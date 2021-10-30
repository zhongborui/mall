package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.arui.mall.product.mapper.SpuSalePropertyNameMapper;
import com.arui.mall.product.service.SpuSalePropertyNameService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * spu销售属性 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class SpuSalePropertyNameServiceImpl extends ServiceImpl<SpuSalePropertyNameMapper, SpuSalePropertyName> implements SpuSalePropertyNameService {

    /**
     * 根据productId查询spu销售属性
     * @param productId
     * @return
     */
    @Override
    public List<SpuSalePropertyName> querySalePropertyByProductId(Long productId) {
        List<SpuSalePropertyName> list = baseMapper.querySalePropertyByProductId(productId);
        return list;
    }
}
