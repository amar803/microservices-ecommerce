package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.domain.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends ElasticsearchRepository<ProductDocument, String> {
    Optional<ProductDocument> findBySku(String sku);

    List<ProductDocument> findByNameContainingIgnoreCase(String name);
}
