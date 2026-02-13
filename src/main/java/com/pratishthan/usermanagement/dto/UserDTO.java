package com.pratishthan.usermanagement.dto;

public record UserDTO(
        Long id,
        String name,
        String email,
        String title,
        String status
) {
}
