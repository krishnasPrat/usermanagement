package com.pratishthan.usermanagement.dto;

public record RolePermissionMappingDTO(
        Long roleId,
        Long permissionId,
        String roleName,
        String permissionName
) {
}
