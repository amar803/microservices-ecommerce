package com.ecommerce.common.tracing;

import java.util.Optional;

public final class CorrelationContext {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static void set(String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            CONTEXT.remove();
            return;
        }
        CONTEXT.set(correlationId.trim());
    }

    public static Optional<String> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    public static String getOrGenerate() {
        return get().orElseGet(() -> {
            String generated = CorrelationIdGenerator.generate();
            set(generated);
            return generated;
        });
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

