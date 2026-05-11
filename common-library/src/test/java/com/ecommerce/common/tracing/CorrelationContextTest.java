package com.ecommerce.common.tracing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorrelationContextTest {

    @AfterEach
    void tearDown() {
        CorrelationContext.clear();
    }

    @Test
    void setShouldStoreAndGetShouldReturnCorrelationId() {
        CorrelationContext.set("corr-123");

        assertTrue(CorrelationContext.get().isPresent());
        assertEquals("corr-123", CorrelationContext.get().orElseThrow());
    }

    @Test
    void clearShouldRemoveStoredCorrelationId() {
        CorrelationContext.set("corr-123");
        CorrelationContext.clear();

        assertTrue(CorrelationContext.get().isEmpty());
    }

    @Test
    void getOrGenerateShouldReturnSameValueAfterFirstCall() {
        String first = CorrelationContext.getOrGenerate();
        String second = CorrelationContext.getOrGenerate();

        assertEquals(first, second);
    }

    @Test
    void setBlankShouldClearContext() {
        CorrelationContext.set("corr-123");
        CorrelationContext.set("   ");

        assertTrue(CorrelationContext.get().isEmpty());
    }
}

