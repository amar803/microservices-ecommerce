package com.ecommerce.common.event;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope<T>(
        String eventType,
        String source,
        String correlationId,
        UUID eventId,
        Instant occurredAt,
        T payload
) {
    public static <T> EventEnvelope<T> of(String eventType, String source, String correlationId, T payload) {
        return new EventEnvelope<>(
                eventType,
                source,
                correlationId,
                UUID.randomUUID(),
                Instant.now(),
                payload
        );
    }
}
