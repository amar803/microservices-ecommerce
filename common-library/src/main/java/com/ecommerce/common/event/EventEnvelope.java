package com.ecommerce.common.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record EventEnvelope<T>(
        String eventType,
        String source,
        String correlationId,
        String schemaVersion,
        Map<String, String> headers,
        UUID eventId,
        Instant occurredAt,
        T payload
) {
    public static <T> EventEnvelope<T> of(String eventType, String source, String correlationId, T payload) {
        return of(eventType, source, correlationId, "1.0", Map.of(), payload);
    }

    public static <T> EventEnvelope<T> of(
            String eventType,
            String source,
            String correlationId,
            String schemaVersion,
            Map<String, String> headers,
            T payload
    ) {
        return new EventEnvelope<>(
                eventType,
                source,
                correlationId,
                schemaVersion,
                headers == null ? Map.of() : Map.copyOf(headers),
                UUID.randomUUID(),
                Instant.now(),
                payload
        );
    }
}
