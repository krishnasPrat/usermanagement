package com.pratishthan.usermanagement.dto;

import java.util.List;

public record ServiceDTO(
        Long id,
        String name,
        String status,
        List<RoleDTO> roles,
        List<PermissionDTO> permissions,
        List<RolePermissionMappingDTO> rolePermissionMappings
) {
}
