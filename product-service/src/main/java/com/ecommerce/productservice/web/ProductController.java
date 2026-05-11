package com.ecommerce.productservice.web;

import com.ecommerce.common.api.ApiResponse;
import com.ecommerce.common.dto.ProductDto;
import com.ecommerce.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto productDto = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(productDto));
    }

    @GetMapping("/{sku}")
    public ResponseEntity<ApiResponse<ProductDto>> getBySku(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getBySku(sku)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDto>>> searchByName(@RequestParam("q") String query) {
        return ResponseEntity.ok(ApiResponse.ok(productService.searchByName(query)));
    }
}
