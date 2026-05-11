package com.ecommerce.orderservice.repository;

import com.ecommerce.orderservice.domain.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
