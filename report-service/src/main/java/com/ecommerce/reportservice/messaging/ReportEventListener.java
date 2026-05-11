package com.ecommerce.reportservice.messaging;

import com.ecommerce.common.messaging.KafkaTopics;
import com.ecommerce.reportservice.service.ReportService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReportEventListener {

    private final ReportService reportService;

    public ReportEventListener(ReportService reportService) {
        this.reportService = reportService;
    }

    @KafkaListener(topics = KafkaTopics.ORDERS_EVENTS, groupId = "report-service")
    public void onOrderEvent(String payload) {
        reportService.recordEvent("ORDER_EVENT");
    }

    @KafkaListener(topics = KafkaTopics.PAYMENTS_EVENTS, groupId = "report-service")
    public void onPaymentEvent(String payload) {
        reportService.recordEvent("PAYMENT_CAPTURED");
    }
}

