package com.ecommerce.productservice.service;

import com.ecommerce.common.dto.ProductDto;
import com.ecommerce.common.exception.ConflictException;
import com.ecommerce.common.exception.NotFoundException;
import com.ecommerce.productservice.domain.ProductDocument;
import com.ecommerce.productservice.repository.ProductRepository;
import com.ecommerce.productservice.web.CreateProductRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @CacheEvict(cacheNames = "productsBySku", key = "#request.sku().trim().toUpperCase()")
    public ProductDto create(CreateProductRequest request) {
        String sku = normalizeSku(request.sku());
        if (productRepository.findBySku(sku).isPresent()) {
            throw new ConflictException("Product already exists for sku: " + sku);
        }

        ProductDocument document = new ProductDocument();
        document.setId(sku);
        document.setSku(sku);
        document.setName(request.name().trim());
        document.setDescription(request.description() == null ? null : request.description().trim());
        document.setPrice(request.price());
        document.setActive(true);

        ProductDocument saved = productRepository.save(document);
        return toDto(saved);
    }

    @Cacheable(cacheNames = "productsBySku", key = "#sku.trim().toUpperCase()")
    public ProductDto getBySku(String sku) {
        ProductDocument document = productRepository.findBySku(normalizeSku(sku))
                .orElseThrow(() -> new NotFoundException("Product not found for sku: " + sku));
        return toDto(document);
    }

    public List<ProductDto> searchByName(String q) {
        return productRepository.findByNameContainingIgnoreCase(q.trim())
                .stream()
                .map(this::toDto)
                .toList();
    }

    private String normalizeSku(String sku) {
        return sku.trim().toUpperCase();
    }

    private ProductDto toDto(ProductDocument document) {
        return new ProductDto(
                null,
                document.getSku(),
                document.getName(),
                document.getDescription(),
                document.getPrice(),
                document.isActive()
        );
    }
}
