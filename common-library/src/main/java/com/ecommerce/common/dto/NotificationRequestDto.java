package com.ecommerce.common.dto;

import java.util.Map;

public record NotificationRequestDto(
        Long userId,
        NotificationChannel channel,
        String template,
        String subject,
        String message,
        Map<String, String> metadata
) {
}
