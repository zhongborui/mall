package com.arui.mall.search.repository;

import com.arui.mall.model.search.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author ...
 */
@Service
public interface SearchRepository extends ElasticsearchRepository<Product, Long> {
}
