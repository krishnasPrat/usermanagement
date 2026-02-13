package com.pratishthan.usermanagement.dto;

import java.time.Instant;
import java.util.List;

public record SubscriptionUserDTO(
        Long id,
        Long subscriptionId,
        Long userId,
        Long roleId,
        String status,
        List<Long> specialPermissionList
) {
}
