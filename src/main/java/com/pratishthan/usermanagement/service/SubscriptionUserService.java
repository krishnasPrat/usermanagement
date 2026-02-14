package com.pratishthan.usermanagement.service;

import com.pratishthan.usermanagement.dto.PermissionListDTO;
import com.pratishthan.usermanagement.dto.SpecialPermissionDTO;
import com.pratishthan.usermanagement.dto.SubscriptionUserDTO;
import java.util.List;

public interface SubscriptionUserService {
    SubscriptionUserDTO createSubscriptionUser(SubscriptionUserDTO subscriptionUser);
    PermissionListDTO getPermissionsForSubscriptionUser(Long subscriptionId, Long userId);
    SubscriptionUserDTO upsertSpecialPermissions(Long subscriptionId, Long userId, List<SpecialPermissionDTO> permissions);
}
