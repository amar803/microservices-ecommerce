package com.ecommerce.paymentservice.service;

import com.ecommerce.common.dto.PaymentDto;
import com.ecommerce.common.dto.PaymentStatus;
import com.ecommerce.common.exception.NotFoundException;
import com.ecommerce.common.exception.ValidationException;
import com.ecommerce.paymentservice.domain.PaymentEntity;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.ecommerce.paymentservice.web.CreatePaymentRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentDto create(CreatePaymentRequest request) {
        return paymentRepository.findByIdempotencyKey(request.idempotencyKey().trim())
                .map(this::toDto)
                .orElseGet(() -> {
                    PaymentEntity entity = new PaymentEntity();
                    entity.setOrderId(request.orderId());
                    entity.setAmount(request.amount());
                    entity.setCurrency(request.currency().trim().toUpperCase());
                    entity.setStatus(PaymentStatus.AUTHORIZED);
                    entity.setIdempotencyKey(request.idempotencyKey().trim());
                    entity.setProviderReference("prov-" + UUID.randomUUID());

                    return toDto(paymentRepository.save(entity));
                });
    }

    @Transactional(readOnly = true)
    public PaymentDto getById(Long paymentId) {
        PaymentEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));
        return toDto(entity);
    }

    @Transactional
    public PaymentDto capture(Long paymentId) {
        PaymentEntity entity = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + paymentId));

        if (entity.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new ValidationException("Only AUTHORIZED payments can be captured");
        }

        entity.setStatus(PaymentStatus.CAPTURED);
        return toDto(paymentRepository.save(entity));
    }

    private PaymentDto toDto(PaymentEntity entity) {
        return new PaymentDto(
                entity.getId(),
                entity.getOrderId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getIdempotencyKey()
        );
    }
}

