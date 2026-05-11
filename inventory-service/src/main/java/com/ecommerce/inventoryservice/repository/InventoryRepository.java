package com.ecommerce.inventoryservice.repository;

import com.ecommerce.inventoryservice.domain.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItemEntity, Long> {

    Optional<InventoryItemEntity> findByProductId(Long productId);

    Optional<InventoryItemEntity> findBySkuIgnoreCase(String sku);

    boolean existsByProductId(Long productId);
}

