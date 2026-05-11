package com.ecommerce.notificationservice.messaging;

import com.ecommerce.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "orders.events", groupId = "notification-service")
    public void onOrderEvent(String payload) {
        notificationService.acceptEvent("orders.events", payload);
    }

    @KafkaListener(topics = "payments.events", groupId = "notification-service")
    public void onPaymentEvent(String payload) {
        notificationService.acceptEvent("payments.events", payload);
    }
}

