package com.ecommerce.common.tracing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorrelationIdGeneratorTest {

    @Test
    void generateShouldReturnNonBlankUuidLikeValue() {
        String id = CorrelationIdGenerator.generate();

        assertFalse(id.isBlank());
        assertTrue(id.contains("-"));
    }

    @Test
    void generateShouldReturnDifferentValuesAcrossCalls() {
        String first = CorrelationIdGenerator.generate();
        String second = CorrelationIdGenerator.generate();

        assertNotEquals(first, second);
    }
}

