package com.ecommerce.notificationservice.service;

import com.ecommerce.common.dto.NotificationRequestDto;
import com.ecommerce.notificationservice.domain.NotificationLogEntity;
import com.ecommerce.notificationservice.repository.NotificationLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationLogRepository notificationLogRepository;
    private final ObjectMapper objectMapper;

    public NotificationService(NotificationLogRepository notificationLogRepository, ObjectMapper objectMapper) {
        this.notificationLogRepository = notificationLogRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public NotificationRecord send(NotificationRequestDto request) {
        NotificationLogEntity entity = new NotificationLogEntity();
        entity.setUserId(request.userId());
        entity.setChannel(request.channel().name());
        entity.setSubject(request.subject());
        entity.setMessage(request.message());
        entity.setMetadataJson(serializeMetadata(request.metadata() == null ? Map.of() : request.metadata()));
        NotificationLogEntity saved = notificationLogRepository.save(entity);

        NotificationRecord record = toRecord(saved);
        log.info("Notification sent to userId={} channel={} subject={}", record.userId(), record.channel(), record.subject());
        return record;
    }

    @Transactional
    public NotificationRecord acceptEvent(String eventType, String payload) {
        NotificationLogEntity entity = new NotificationLogEntity();
        entity.setUserId(null);
        entity.setChannel("EVENT");
        entity.setSubject(eventType);
        entity.setMessage(payload);
        entity.setMetadataJson("{}");

        return toRecord(notificationLogRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<NotificationRecord> recent() {
        return notificationLogRepository.findTop50ByOrderByCreatedAtDesc()
                .stream()
                .map(this::toRecord)
                .toList();
    }

    private NotificationRecord toRecord(NotificationLogEntity entity) {
        return new NotificationRecord(
                entity.getCreatedAt(),
                entity.getUserId(),
                entity.getChannel(),
                entity.getSubject(),
                entity.getMessage(),
                deserializeMetadata(entity.getMetadataJson())
        );
    }

    private String serializeMetadata(Map<String, String> metadata) {
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }

    private Map<String, String> deserializeMetadata(String raw) {
        try {
            return objectMapper.readValue(raw, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return Map.of();
        }
    }

    public record NotificationRecord(
            Instant timestamp,
            Long userId,
            String channel,
            String subject,
            String message,
            Map<String, String> metadata
    ) {
    }
}

