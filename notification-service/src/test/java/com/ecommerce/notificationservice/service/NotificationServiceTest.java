package com.ecommerce.notificationservice.service;

import com.ecommerce.common.dto.NotificationChannel;
import com.ecommerce.common.dto.NotificationRequestDto;
import com.ecommerce.notificationservice.domain.NotificationLogEntity;
import com.ecommerce.notificationservice.repository.NotificationLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationLogRepository, new ObjectMapper());
    }

    @Test
    void sendShouldPersistNotificationAndReturnRecord() {
        when(notificationLogRepository.save(any(NotificationLogEntity.class))).thenAnswer(invocation -> {
            NotificationLogEntity entity = invocation.getArgument(0);
            ReflectionTestUtils.setField(entity, "id", 10L);
            ReflectionTestUtils.setField(entity, "createdAt", Instant.parse("2026-05-10T00:00:00Z"));
            return entity;
        });

        NotificationRequestDto request = new NotificationRequestDto(
                7L,
                NotificationChannel.EMAIL,
                "order-template",
                "Order confirmed",
                "Your order is confirmed",
                Map.of("orderId", "5001")
        );

        NotificationService.NotificationRecord record = notificationService.send(request);

        assertEquals(7L, record.userId());
        assertEquals("EMAIL", record.channel());
        assertEquals("Order confirmed", record.subject());
    }
}

