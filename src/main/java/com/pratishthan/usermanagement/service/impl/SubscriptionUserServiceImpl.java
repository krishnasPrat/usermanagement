package com.pratishthan.usermanagement.service.impl;

import com.pratishthan.usermanagement.dto.PermissionDTO;
import com.pratishthan.usermanagement.dto.PermissionListDTO;
import com.pratishthan.usermanagement.dto.SubscriptionUserDTO;
import com.pratishthan.usermanagement.entity.PermissionEntity;
import com.pratishthan.usermanagement.entity.RoleEntity;
import com.pratishthan.usermanagement.entity.ServiceRolePermissionEntity;
import com.pratishthan.usermanagement.entity.SubscriptionEntity;
import com.pratishthan.usermanagement.entity.SubscriptionUserEntity;
import com.pratishthan.usermanagement.mapper.PermissionMapper;
import com.pratishthan.usermanagement.repository.PermissionRepository;
import com.pratishthan.usermanagement.repository.RoleRepository;
import com.pratishthan.usermanagement.repository.ServiceRolePermissionRepository;
import com.pratishthan.usermanagement.repository.SubscriptionRepository;
import com.pratishthan.usermanagement.repository.SubscriptionUserRepository;
import com.pratishthan.usermanagement.repository.UserRepository;
import com.pratishthan.usermanagement.service.SubscriptionUserService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionUserServiceImpl implements SubscriptionUserService {

    private final SubscriptionUserRepository subscriptionUserRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ServiceRolePermissionRepository serviceRolePermissionRepository;
    private final PermissionMapper permissionMapper;


    public SubscriptionUserServiceImpl(SubscriptionUserRepository subscriptionUserRepository, SubscriptionRepository subscriptionRepository, UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, ServiceRolePermissionRepository serviceRolePermissionRepository, PermissionMapper permissionMapper) {
        this.subscriptionUserRepository = subscriptionUserRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.serviceRolePermissionRepository = serviceRolePermissionRepository;
        this.permissionMapper = permissionMapper;
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

    @Override
    public PermissionListDTO getPermissionsForSubscriptionUser(Long subscriptionId, Long userId) {
        SubscriptionUserEntity subscriptionUser = subscriptionUserRepository
                .findBySubscriptionIdAndUserId(subscriptionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("SubscriptionUserEntity not found"));

        Set<Long> permissionIds = new HashSet<>();
        List<ServiceRolePermissionEntity> defaults = serviceRolePermissionRepository
                .findByRoleId(subscriptionUser.getRoleId());
        for (ServiceRolePermissionEntity mapping : defaults) {
            permissionIds.add(mapping.getPermissionId());
        }
        if (subscriptionUser.getSpecialPermissionList() != null) {
            permissionIds.addAll(subscriptionUser.getSpecialPermissionList());
        }

        List<PermissionDTO> permissions = new ArrayList<>();
        if (!permissionIds.isEmpty()) {
            List<PermissionEntity> entities = permissionRepository.findAllById(permissionIds);
            for (PermissionEntity entity : entities) {
                permissions.add(permissionMapper.toDTO(entity));
            }
        }

        return new PermissionListDTO(permissions);
    }
}
