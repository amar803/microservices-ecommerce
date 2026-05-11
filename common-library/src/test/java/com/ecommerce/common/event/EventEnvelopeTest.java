package com.ecommerce.common.event;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EventEnvelopeTest {

    @Test
    void defaultFactoryShouldPopulateVersionAndEmptyHeaders() {
        EventEnvelope<String> envelope = EventEnvelope.of(
                "ORDER_CREATED",
                "order-service",
                "corr-123",
                "payload"
        );

        assertEquals("1.0", envelope.schemaVersion());
        assertFalse(envelope.headers() == null);
        assertEquals(0, envelope.headers().size());
    }

    @Test
    void extendedFactoryShouldCopyHeadersAndVersion() {
        EventEnvelope<String> envelope = EventEnvelope.of(
                "ORDER_CREATED",
                "order-service",
                "corr-123",
                "2.0",
                Map.of("tenant", "default"),
                "payload"
        );

        assertEquals("2.0", envelope.schemaVersion());
        assertEquals("default", envelope.headers().get("tenant"));
    }
}

