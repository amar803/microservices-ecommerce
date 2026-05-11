package com.ecommerce.inventoryservice.web;

import com.ecommerce.common.api.ApiResponse;
import com.ecommerce.common.dto.InventoryReservationDto;
import com.ecommerce.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<InventoryItemView>> upsertItem(@Valid @RequestBody CreateInventoryItemRequest request) {
        InventoryItemView item = inventoryService.upsert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(item));
    }

    @GetMapping("/items/product/{productId}")
    public ResponseEntity<ApiResponse<InventoryItemView>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getByProductId(productId)));
    }

    @GetMapping("/items/sku/{sku}")
    public ResponseEntity<ApiResponse<InventoryItemView>> getBySku(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getBySku(sku)));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<InventoryReservationDto>> reserve(@Valid @RequestBody ReserveInventoryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.reserve(request)));
    }

    @PostMapping("/release")
    public ResponseEntity<ApiResponse<InventoryItemView>> release(@Valid @RequestBody ReleaseInventoryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.release(request)));
    }
}

