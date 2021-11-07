package com.arui.mall.search.repository;

import com.arui.mall.model.search.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ...
 */
@Repository
public interface SearchRepository extends ElasticsearchRepository<Product, Long> {
}
