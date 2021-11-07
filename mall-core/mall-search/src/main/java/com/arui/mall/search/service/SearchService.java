package com.arui.mall.search.service;

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

}
