package com.ecommerce.paymentservice.web;

import com.ecommerce.common.api.ApiResponse;
import com.ecommerce.common.dto.PaymentDto;
import com.ecommerce.paymentservice.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDto>> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentDto created = paymentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getById(paymentId)));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<ApiResponse<PaymentDto>> capturePayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.capture(paymentId)));
    }
}

