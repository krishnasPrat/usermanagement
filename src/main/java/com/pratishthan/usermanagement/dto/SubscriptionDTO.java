package com.pratishthan.usermanagement.dto;

import java.time.Instant;

public record SubscriptionDTO(
        Long id,
        Long userId,
        Long serviceId,
        String status
) {
}
