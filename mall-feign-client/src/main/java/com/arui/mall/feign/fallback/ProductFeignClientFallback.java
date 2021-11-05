package com.arui.mall.feign.fallback;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.entity.BaseCategoryView;
import com.arui.mall.model.pojo.entity.SpuSalePropertyName;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author ...
 */
@Component
public class ProductFeignClientFallback implements ProductFeignClient {
    @Override
    public SkuInfoVO getSkuDetailById(Long skuId) {
        return null;
    }

    @Override
    public BaseCategoryView getCategoryViewByCategory3Id(Long category3Id) {
        return null;
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return null;
    }

    @Override
    public List<SpuSalePropertyName> getSpuSPNAndSkuSPNSelected(Long spuId, Long skuId) {
        return null;
    }

    @Override
    public List<Map> getSpuSPVAndSkuMapping(Long spuId) {
        return null;
    }

    @Override
    public List<JSONObject> getBaseCategoryList() {
        return null;
    }
}
