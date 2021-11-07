package com.arui.mall.search.service;

import com.arui.mall.model.search.SearchParam;
import com.arui.mall.model.search.SearchResponseVO;

/**
 * @author ...
 */
public interface SearchService {
    /**
     * 上架
     * @param skuId
     */
    void onSale(Long skuId);

    /**
     * 下架
     * @param skuId
     */
    void offSale(Long skuId);

    /**
     * 增加热度
     * @param skuId
     */
    void incrHostScore(Long skuId);

    /**
     * 商品搜索
     * @param searchParam
     * @return
     */
    SearchResponseVO searchProduct(SearchParam searchParam);
}
