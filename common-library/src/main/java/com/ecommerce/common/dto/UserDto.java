package com.ecommerce.common.dto;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        boolean active
) {
}
