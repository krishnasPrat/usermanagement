package com.pratishthan.usermanagement.dto;

public record PermissionDTO(
        Long id,
        Long serviceId,
        String name,
        String description
) {
}
