package com.pratishthan.usermanagement.dto;

public record UserPermissionDTO(
        Long subscriptionId,
        String subscriptionOwnerName,
        String serviceName,
        String permissionName,
        String access
) {
}
