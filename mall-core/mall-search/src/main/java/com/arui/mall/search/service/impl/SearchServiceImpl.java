package com.arui.mall.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.model.pojo.entity.BaseBrand;
import com.arui.mall.model.pojo.entity.BaseCategoryView;
import com.arui.mall.model.pojo.vo.PlatformPropertyVO;
import com.arui.mall.model.pojo.vo.PlatformPropertyValueVO;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.arui.mall.model.search.*;
import com.arui.mall.search.repository.SearchRepository;
import com.arui.mall.search.service.SearchService;
import lombok.SneakyThrows;
import org.apache.commons.lang.ArrayUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
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

    @Resource
    private RestHighLevelClient restHighLevelClient;

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

    @SneakyThrows
    @Override
    public SearchResponseVO searchProduct(SearchParam searchParam) {
        // 拼接DSL语句， 返回一个rest请求
        SearchRequest searchRequest = this.buildDSL(searchParam);
        // 执行语句，返回结果
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 处理结果到实体类
        SearchResponseVO searchResponseVO = this.parseSearchResponse(searchResponse);

        // 处理其他参数, 分页信息
        // 每页多少条
        Integer pageSize = searchParam.getPageSize();
        searchResponseVO.setPageSize(pageSize);
        // 当前页
        Integer pageNo = searchParam.getPageNo();
        searchResponseVO.setPageNo(pageNo);
        // toTalPages总共多少页, total总共多少条数据
        Long total = searchResponseVO.getTotal();
        Long toTalPages = (total % pageSize == 0) ? total / pageSize : total / pageSize + 1;
        searchResponseVO.setTotalPages(toTalPages);

        // 返回结果
        return searchResponseVO;
    }

    /**
     * 解析结果到实体类
     * @return
     */
    private SearchResponseVO parseSearchResponse(SearchResponse searchResponse) {
        SearchResponseVO searchResponseVO = new SearchResponseVO();

        // 1.基本信息的集合
        SearchHits firstHits = searchResponse.getHits();
        SearchHit[] secondHits = firstHits.getHits();
        // 返回前端的基本信息集合
        List<Product> productList = new ArrayList<>();
        if (!ArrayUtils.isEmpty(secondHits)) {
            for (SearchHit secondHit : secondHits) {
                String sourceAsString = secondHit.getSourceAsString();
                Product product = JSONObject.parseObject(sourceAsString, Product.class);

                // 替换高亮字段
                HighlightField productName = secondHit.getHighlightFields().get("productName");
                if (!StringUtils.isEmpty(productName)){
                    Text fragment = productName.getFragments()[0];
                    product.setProductName(fragment.toString());
                }
            productList.add(product);
            }
        }

        searchResponseVO.setProductList(productList);
        searchResponseVO.setTotal(firstHits.getTotalHits());

        // 2.品牌聚合数据信息
        // 先获取品牌id聚合信息，再迭代获取子聚合
        ParsedLongTerms brandIdAgg = searchResponse.getAggregations().get("brandIdAgg");
        List<SearchBrandVo> brandVoList = brandIdAgg.getBuckets().stream().map(bucket -> {
            SearchBrandVo searchBrandVo = new SearchBrandVo();
            // 获取品牌id值
            String keyAsString = bucket.getKeyAsString();
            searchBrandVo.setBrandId(Long.parseLong(keyAsString));

            // 获取子聚合
            ParsedStringTerms brandLogoUrlAgg = bucket.getAggregations().get("brandLogoUrlAgg");
            String brandLogoUrl = brandLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            searchBrandVo.setBrandLogoUrl(brandLogoUrl);

            return searchBrandVo;
        }).collect(Collectors.toList());
        searchResponseVO.setBrandVoList(brandVoList);

        // 3.嵌套平台属性
        ParsedNested platformPropertyAgg = searchResponse.getAggregations().get("platformPropertyAgg");
        ParsedLongTerms propertyKeyIdAgg = platformPropertyAgg.getAggregations().get("propertyKeyIdAgg");

        List<? extends Terms.Bucket> buckets = propertyKeyIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(buckets)) {

            List<SearchPlatformPropertyVo> platformPropertyList  = buckets.stream().map(bucket -> {
                SearchPlatformPropertyVo searchPlatformPropertyVo = new SearchPlatformPropertyVo();

                String keyAsString = bucket.getKeyAsString();
                searchPlatformPropertyVo.setPropertyKeyId(Long.parseLong(keyAsString));

                // 获取聚合信息
                ParsedStringTerms propertyKeyAgg = bucket.getAggregations().get("propertyKeyAgg");
                String propertyKey = propertyKeyAgg.getBuckets().get(0).getKeyAsString();
                searchPlatformPropertyVo.setPropertyKey(propertyKey);

                // 获取平台属性值聚合信息
                ParsedStringTerms propertyValueAgg = bucket.getAggregations().get("propertyValueAgg");
                List<? extends Terms.Bucket> propertyValueAggBuckets = propertyValueAgg.getBuckets();
                if (!CollectionUtils.isEmpty(propertyValueAggBuckets)) {
                    List<String> stringList = propertyValueAggBuckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
                    searchPlatformPropertyVo.setPropertyValueList(stringList);
                }
                return searchPlatformPropertyVo;

            }).collect(Collectors.toList());

            searchResponseVO.setPlatformPropertyList(platformPropertyList);
        }

        return searchResponseVO;
    }

    /**
     * // 拼接DSL语句
     * @param searchParam
     * @return
     */
    private SearchRequest buildDSL(SearchParam searchParam) {
        // 1. 构建bool字段
        BoolQueryBuilder fistBool = QueryBuilders.boolQuery();

        // 2.构建分类Id过滤器
        if (!StringUtils.isEmpty(searchParam.getCategory3Id())){
            TermQueryBuilder category3Id = QueryBuilders.termQuery("category3Id", searchParam.getCategory3Id());
            fistBool.filter(category3Id);
        }

        // 3.构建品牌id过滤条件
        String brandName = searchParam.getBrandName();
        if (!StringUtils.isEmpty(brandName)){
            String[] brandNames = brandName.split(":");
            if (!ArrayUtils.isEmpty(brandNames)){
                TermQueryBuilder brandId = QueryBuilders.termQuery("brandId", brandNames[0]);
                fistBool.filter(brandId);
            }
        }

        // 4.构建平台属性条件, 需要嵌套查询，nest
        // http://search.gmall.com/search.html?keyword=三星&props=23:4G:运行内存&props=24:128G:机身内存
        String[] props = searchParam.getProps();
        if (!ArrayUtils.isEmpty(props)){
            for (String prop : props) {
                String[] propSplit = prop.split(":");
                if (!ArrayUtils.isEmpty(propSplit)){
                    // a.构建bool
                    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                    // 构建子查询条件bool
                    BoolQueryBuilder childBoolQuery = QueryBuilders.boolQuery();
                    // 构建must
                    childBoolQuery.must(QueryBuilders.termQuery("platformProperty.propertyKeyId", propSplit[0]));
                    childBoolQuery.must(QueryBuilders.termQuery("platformProperty.propertyValue", propSplit[1]));
                    // 构建must下面的嵌套
                    boolQueryBuilder.must(QueryBuilders.nestedQuery("platformProperty", childBoolQuery, ScoreMode.None));
                    // 添加到firstBool中
                    fistBool.filter(boolQueryBuilder);
                }
            }
        }

        // 构建关键字（商品名称查询）
        String keyword = searchParam.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            MatchQueryBuilder productName = QueryBuilders.matchQuery("productName", keyword).operator(Operator.AND);
            fistBool.must(productName);
        }

        // 构建dsl查询器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(fistBool);

        // 排序, 如果不传参数， 默认 热度降序排序
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)){
            String[] split = order.split(":");
            if (split != null && split.length == 2){
                String filed = null;
                switch (split[0]) {
                    case "1":
                        filed = "hostScore";
                        break;
                    case "2":
                        filed = "price";
                        break;
                }
                searchSourceBuilder.sort(filed, "asc".equals(split[1]) ? SortOrder.ASC : SortOrder.DESC);
            }else {
                searchSourceBuilder.sort("hostScore", SortOrder.DESC);
            }
        }

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("productName")
                .preTags("<span style=color:red>")
                .postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        // 品牌聚合
        TermsAggregationBuilder brandIdAggBuilder = AggregationBuilders.terms("brandIdAgg")
                .field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName"))
                .subAggregation(AggregationBuilders.terms("brandLogoUrlAgg").field("brandLogoUrl"));
        searchSourceBuilder.aggregation(brandIdAggBuilder);

        // 平台属性聚合(嵌套nest)
        TermsAggregationBuilder propAggBuilder = AggregationBuilders.terms("propertyKeyIdAgg")
                .field("platformProperty.propertyKeyId")
                .subAggregation(AggregationBuilders.terms("propertyKeyAgg").field("platformProperty.propertyKey"))
                .subAggregation(AggregationBuilders.terms("propertyValueAgg").field("platformProperty.propertyValue"));
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("platformPropertyAgg", "platformProperty");
        nestedAggregationBuilder.subAggregation(propAggBuilder);
        searchSourceBuilder.aggregation(nestedAggregationBuilder);

        // 设置要查询的字段
        searchSourceBuilder.fetchSource(new String[]{"id", "defaultImage", "productName", "price"}, null);
        // 设置要查询的index和type
        SearchRequest searchRequest = new SearchRequest("product");
        searchRequest.types("info");
        // 返回查询的请求
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

}
