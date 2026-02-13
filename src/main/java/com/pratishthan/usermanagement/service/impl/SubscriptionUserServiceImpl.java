package com.pratishthan.usermanagement.service.impl;

import com.pratishthan.usermanagement.dto.SubscriptionUserDTO;
import com.pratishthan.usermanagement.entity.PermissionEntity;
import com.pratishthan.usermanagement.entity.RoleEntity;
import com.pratishthan.usermanagement.entity.SubscriptionEntity;
import com.pratishthan.usermanagement.entity.SubscriptionUserEntity;
import com.pratishthan.usermanagement.repository.PermissionRepository;
import com.pratishthan.usermanagement.repository.RoleRepository;
import com.pratishthan.usermanagement.repository.SubscriptionRepository;
import com.pratishthan.usermanagement.repository.SubscriptionUserRepository;
import com.pratishthan.usermanagement.repository.UserRepository;
import com.pratishthan.usermanagement.service.SubscriptionUserService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionUserServiceImpl implements SubscriptionUserService {

    private final SubscriptionUserRepository subscriptionUserRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public SubscriptionUserServiceImpl(SubscriptionUserRepository subscriptionUserRepository, SubscriptionRepository subscriptionRepository, UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.subscriptionUserRepository = subscriptionUserRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public SubscriptionUserDTO createSubscriptionUser(SubscriptionUserDTO subscriptionUser) {
        if (subscriptionUser == null
                || subscriptionUser.subscriptionId() == null
                || subscriptionUser.userId() == null
                || subscriptionUser.roleId() == null) {
            throw new IllegalArgumentException("subscriptionId, userId, and roleId are required");
        }

        SubscriptionEntity subscription = subscriptionRepository.findById(subscriptionUser.subscriptionId())
                .orElseThrow(() -> new IllegalArgumentException("SubscriptionEntity not found"));
        if (!userRepository.existsById(subscriptionUser.userId())) {
            throw new IllegalArgumentException("UserEntity not found");
        }

        RoleEntity role = roleRepository.findById(subscriptionUser.roleId())
                .orElseThrow(() -> new IllegalArgumentException("RoleEntity not found"));
        if (!role.getServiceId().equals(subscription.getServiceId())) {
            throw new IllegalArgumentException("RoleEntity does not belong to the subscription service");
        }

        List<Long> validatedPermissions = new ArrayList<>();
        if (subscriptionUser.specialPermissionList() != null) {
            for (Long permissionId : subscriptionUser.specialPermissionList()) {
                if (permissionId == null) {
                    continue;
                }
                PermissionEntity permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new IllegalArgumentException("PermissionEntity not found"));
                if (!permission.getServiceId().equals(subscription.getServiceId())) {
                    throw new IllegalArgumentException("PermissionEntity does not belong to the subscription service");
                }
                validatedPermissions.add(permission.getId());
            }
        }

        SubscriptionUserEntity entity = new SubscriptionUserEntity();
        entity.setSubscriptionId(subscriptionUser.subscriptionId());
        entity.setUserId(subscriptionUser.userId());
        entity.setRoleId(subscriptionUser.roleId());
        entity.setStatus(subscriptionUser.status());
        entity.setSpecialPermissionList(validatedPermissions);

        SubscriptionUserEntity saved = subscriptionUserRepository.save(entity);

        return new SubscriptionUserDTO(
                saved.getId(),
                saved.getSubscriptionId(),
                saved.getUserId(),
                saved.getRoleId(),
                saved.getStatus(),
                saved.getSpecialPermissionList()
        );
    }
}
