package com.pratishthan.usermanagement.dto;

public record RoleDTO(
        Long id,
        Long serviceId,
        String name,
        String description
) {
}
