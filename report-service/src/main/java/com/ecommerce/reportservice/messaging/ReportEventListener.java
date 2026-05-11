package com.ecommerce.reportservice.messaging;

import com.ecommerce.reportservice.service.ReportService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReportEventListener {

    private final ReportService reportService;

    public ReportEventListener(ReportService reportService) {
        this.reportService = reportService;
    }

    @KafkaListener(topics = "orders.events", groupId = "report-service")
    public void onOrderEvent(String payload) {
        reportService.recordEvent("ORDER_EVENT");
    }

    @KafkaListener(topics = "payments.events", groupId = "report-service")
    public void onPaymentEvent(String payload) {
        reportService.recordEvent("PAYMENT_CAPTURED");
    }
}

