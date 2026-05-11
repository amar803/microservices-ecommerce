package com.ecommerce.inventoryservice.service;

import com.ecommerce.common.dto.InventoryReservationDto;
import com.ecommerce.common.exception.NotFoundException;
import com.ecommerce.common.exception.ValidationException;
import com.ecommerce.inventoryservice.domain.InventoryItemEntity;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import com.ecommerce.inventoryservice.web.CreateInventoryItemRequest;
import com.ecommerce.inventoryservice.web.InventoryItemView;
import com.ecommerce.inventoryservice.web.ReleaseInventoryRequest;
import com.ecommerce.inventoryservice.web.ReserveInventoryRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryItemView upsert(CreateInventoryItemRequest request) {
        InventoryItemEntity entity = inventoryRepository.findByProductId(request.productId())
                .orElseGet(InventoryItemEntity::new);

        entity.setProductId(request.productId());
        entity.setSku(request.sku().trim().toUpperCase());
        entity.setAvailableQuantity(request.availableQuantity());
        if (entity.getReservedQuantity() > request.availableQuantity()) {
            entity.setReservedQuantity(request.availableQuantity());
        }

        InventoryItemEntity saved = inventoryRepository.save(entity);
        return toView(saved);
    }

    @Transactional(readOnly = true)
    public InventoryItemView getByProductId(Long productId) {
        return toView(requireByProductId(productId));
    }

    @Transactional(readOnly = true)
    public InventoryItemView getBySku(String sku) {
        InventoryItemEntity entity = inventoryRepository.findBySkuIgnoreCase(sku.trim())
                .orElseThrow(() -> new NotFoundException("Inventory item not found for sku: " + sku));
        return toView(entity);
    }

    @Transactional
    public InventoryReservationDto reserve(ReserveInventoryRequest request) {
        InventoryItemEntity entity = requireByProductId(request.productId());
        validateSku(entity, request.sku());

        int remaining = entity.getAvailableQuantity() - entity.getReservedQuantity();
        if (remaining < request.quantity()) {
            return new InventoryReservationDto(
                    entity.getProductId(),
                    entity.getSku(),
                    request.quantity(),
                    0,
                    false
            );
        }

        entity.setReservedQuantity(entity.getReservedQuantity() + request.quantity());
        inventoryRepository.save(entity);

        return new InventoryReservationDto(
                entity.getProductId(),
                entity.getSku(),
                request.quantity(),
                request.quantity(),
                true
        );
    }

    @Transactional
    public InventoryItemView release(ReleaseInventoryRequest request) {
        InventoryItemEntity entity = requireByProductId(request.productId());
        validateSku(entity, request.sku());

        int newReserved = Math.max(0, entity.getReservedQuantity() - request.quantity());
        entity.setReservedQuantity(newReserved);

        return toView(inventoryRepository.save(entity));
    }

    private InventoryItemEntity requireByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Inventory item not found for productId: " + productId));
    }

    private void validateSku(InventoryItemEntity entity, String sku) {
        if (!entity.getSku().equalsIgnoreCase(sku.trim())) {
            throw new ValidationException("SKU does not match productId mapping");
        }
    }

    private InventoryItemView toView(InventoryItemEntity entity) {
        return new InventoryItemView(
                entity.getId(),
                entity.getProductId(),
                entity.getSku(),
                entity.getAvailableQuantity(),
                entity.getReservedQuantity()
        );
    }
}

