package com.ecommerce.notificationservice.messaging;

import com.ecommerce.common.messaging.KafkaTopics;
import com.ecommerce.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = KafkaTopics.ORDERS_EVENTS, groupId = "notification-service")
    public void onOrderEvent(String payload) {
        notificationService.acceptEvent(KafkaTopics.ORDERS_EVENTS, payload);
    }

    @KafkaListener(topics = KafkaTopics.PAYMENTS_EVENTS, groupId = "notification-service")
    public void onPaymentEvent(String payload) {
        notificationService.acceptEvent(KafkaTopics.PAYMENTS_EVENTS, payload);
    }
}

