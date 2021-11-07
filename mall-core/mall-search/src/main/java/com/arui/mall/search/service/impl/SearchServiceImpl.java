package com.arui.mall.search.service.impl;

import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.entity.BaseBrand;
import com.arui.mall.model.pojo.entity.BaseCategoryView;
import com.arui.mall.model.pojo.vo.PlatformPropertyVO;
import com.arui.mall.model.pojo.vo.PlatformPropertyValueVO;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.arui.mall.model.search.Product;
import com.arui.mall.model.search.SearchPlatformProperty;
import com.arui.mall.search.repository.SearchRepository;
import com.arui.mall.search.service.SearchService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ...
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private SearchRepository searchRepository;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void onSale(Long skuId) {
        Product product = new Product();
        SkuInfoVO skuDetailById = productFeignClient.getSkuDetailById(skuId);

        if (skuDetailById != null){
            // 基本信息
            product.setId(skuDetailById.getId());
            product.setProductName(skuDetailById.getSkuName());
            product.setCreateTime(new Date());
            product.setPrice(skuDetailById.getPrice().doubleValue());
            product.setDefaultImage(skuDetailById.getSkuDefaultImg());

            // 分类信息
            BaseCategoryView categoryViewByCategory3Id = productFeignClient.getCategoryViewByCategory3Id(skuDetailById.getCategory3Id());
            if (categoryViewByCategory3Id != null){
                product.setCategory1Id(categoryViewByCategory3Id.getCategory1Id());
                product.setCategory1Name(categoryViewByCategory3Id.getCategory1Name());
                product.setCategory2Id(categoryViewByCategory3Id.getCategory2Id());
                product.setCategory2Name(categoryViewByCategory3Id.getCategory2Name());
                product.setCategory3Id(categoryViewByCategory3Id.getCategory3Id());
                product.setCategory3Name(categoryViewByCategory3Id.getCategory3Name());

            }

            // 平台属性信息
            Long category3Id = skuDetailById.getCategory3Id();
            List<PlatformPropertyVO> platformPropertyVOList = productFeignClient.getPlatformProperty(category3Id);
            if (!CollectionUtils.isEmpty(platformPropertyVOList)){
                List<SearchPlatformProperty> searchPlatformPropertyList = platformPropertyVOList.stream().map(platformProperty -> {
                    SearchPlatformProperty searchPlatformProperty = new SearchPlatformProperty();
                    searchPlatformProperty.setPropertyKeyId(platformProperty.getId());
                    searchPlatformProperty.setPropertyKey(platformProperty.getPropertyKey());
                    List<PlatformPropertyValueVO> propertyValueList = platformProperty.getPropertyValueList();
                    String propertyValue = propertyValueList.get(0).getPropertyValue();
                    searchPlatformProperty.setPropertyValue(propertyValue);
                    return searchPlatformProperty;
                }).collect(Collectors.toList());
                product.setPlatformProperty(searchPlatformPropertyList);

            }

            // 品牌信息
            BaseBrand brandInfo = productFeignClient.getBrandInfo(skuDetailById.getBrandId());
            if (brandInfo != null){
                product.setBrandId(brandInfo.getId());
                product.setBrandName(brandInfo.getBrandName());
                product.setBrandLogoUrl(brandInfo.getBrandLogoUrl());

            }

        }

        searchRepository.save(product);
    }

    @Override
    public void offSale(Long skuId) {
        searchRepository.deleteById(skuId);
    }

    @Override
    public void incrHostScore(Long skuId) {
        // 引入redis作为缓存
        Double cacheHotScore = redisTemplate.opsForZSet().incrementScore("sku:", "hotScore:" + skuId, 1);

        boolean flag = (cacheHotScore % 10 == 0 )? true : false;

        if (flag){
            Optional<Product> skuById = searchRepository.findById(skuId);
            if (skuById.isPresent()){
                Product product = skuById.get();
                product.setHotScore(Math.round(cacheHotScore));
                searchRepository.save(product);
            }
        }

    }

}
