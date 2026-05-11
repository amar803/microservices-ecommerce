package com.ecommerce.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdempotencyKeyUtilTest {

    @Test
    void fingerprintShouldBeDeterministic() {
        String first = IdempotencyKeyUtil.fingerprint("order-123");
        String second = IdempotencyKeyUtil.fingerprint("order-123");

        assertEquals(first, second);
    }

    @Test
    void fingerprintShouldDifferentiateDifferentKeys() {
        String first = IdempotencyKeyUtil.fingerprint("order-123");
        String second = IdempotencyKeyUtil.fingerprint("order-124");

        assertNotEquals(first, second);
    }

    @Test
    void fingerprintShouldRejectBlankInput() {
        assertThrows(IllegalArgumentException.class, () -> IdempotencyKeyUtil.fingerprint("  "));
    }
}
