package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.domain.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByIdempotencyKey(String idempotencyKey);
}

