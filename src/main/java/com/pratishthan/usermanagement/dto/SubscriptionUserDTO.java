package com.pratishthan.usermanagement.dto;

import java.util.List;

public record SubscriptionUserDTO(
        Long id,
        Long subscriptionId,
        Long userId,
        Long roleId,
        String status,
        List<SpecialPermissionDTO> specialPermissions
) {
}
