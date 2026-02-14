package com.pratishthan.usermanagement.service;

import com.pratishthan.usermanagement.dto.PermissionListDTO;
import com.pratishthan.usermanagement.dto.PermissionUpdateDTO;
import com.pratishthan.usermanagement.dto.SubscriptionUserDTO;

public interface SubscriptionUserService {
    SubscriptionUserDTO createSubscriptionUser(SubscriptionUserDTO subscriptionUser);
    PermissionListDTO getPermissionsForSubscriptionUser(Long subscriptionId, Long userId);
    SubscriptionUserDTO addSpecialPermissions(Long subscriptionId, Long userId, PermissionUpdateDTO update);
    SubscriptionUserDTO removeSpecialPermissions(Long subscriptionId, Long userId, PermissionUpdateDTO update);
}
