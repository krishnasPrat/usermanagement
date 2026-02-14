package com.pratishthan.usermanagement.service.impl;

import com.pratishthan.usermanagement.dto.UserDTO;
import com.pratishthan.usermanagement.dto.UserPermissionDTO;
import com.pratishthan.usermanagement.entity.PermissionEntity;
import com.pratishthan.usermanagement.entity.ServiceEntity;
import com.pratishthan.usermanagement.entity.ServiceRolePermissionEntity;
import com.pratishthan.usermanagement.entity.SpecialPermissionEntity;
import com.pratishthan.usermanagement.entity.SubscriptionEntity;
import com.pratishthan.usermanagement.entity.SubscriptionUserEntity;
import com.pratishthan.usermanagement.entity.UserEntity;
import com.pratishthan.usermanagement.mapper.UserMapper;
import com.pratishthan.usermanagement.repository.PermissionRepository;
import com.pratishthan.usermanagement.repository.ServiceRepository;
import com.pratishthan.usermanagement.repository.ServiceRolePermissionRepository;
import com.pratishthan.usermanagement.repository.SubscriptionRepository;
import com.pratishthan.usermanagement.repository.SubscriptionUserRepository;
import com.pratishthan.usermanagement.repository.UserRepository;
import com.pratishthan.usermanagement.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionUserRepository subscriptionUserRepository;
    private final ServiceRepository serviceRepository;
    private final PermissionRepository permissionRepository;
    private final ServiceRolePermissionRepository serviceRolePermissionRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            SubscriptionRepository subscriptionRepository,
            SubscriptionUserRepository subscriptionUserRepository,
            ServiceRepository serviceRepository,
            PermissionRepository permissionRepository,
            ServiceRolePermissionRepository serviceRolePermissionRepository
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionUserRepository = subscriptionUserRepository;
        this.serviceRepository = serviceRepository;
        this.permissionRepository = permissionRepository;
        this.serviceRolePermissionRepository = serviceRolePermissionRepository;
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO user) {
        var entity = userMapper.toEntity(user);
        var saved = userRepository.save(entity);
        return userMapper.toDTO(saved);
    }

    @Override
    public List<UserPermissionDTO> getUserPermissions(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        List<UserPermissionDTO> result = new ArrayList<>();

        // Owner subscriptions: full access
        List<SubscriptionEntity> ownedSubscriptions = subscriptionRepository.findByUserId(userId);
        Map<Long, String> ownerNameBySubscription = new HashMap<>();
        for (SubscriptionEntity sub : ownedSubscriptions) {
            UserEntity owner = userRepository.findById(sub.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Owner user not found"));
            ownerNameBySubscription.put(sub.getId(), owner.getName());

            ServiceEntity service = serviceRepository.findById(sub.getServiceId())
                    .orElseThrow(() -> new IllegalArgumentException("Service not found"));
            List<PermissionEntity> permissions = permissionRepository.findByServiceId(sub.getServiceId());
            for (PermissionEntity p : permissions) {
                result.add(new UserPermissionDTO(
                        sub.getId(),
                        owner.getName(),
                        service.getName(),
                        p.getName(),
                        "ALLOWED"
                ));
            }
        }

        // Subscriptions where user is a member
        List<SubscriptionUserEntity> membership = subscriptionUserRepository.findByUserId(userId);
        if (membership.isEmpty()) {
            return result;
        }

        Map<Long, SubscriptionEntity> subscriptionById = subscriptionRepository.findAllById(
                membership.stream().map(SubscriptionUserEntity::getSubscriptionId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(SubscriptionEntity::getId, s -> s));

        Map<Long, ServiceEntity> serviceById = serviceRepository.findAllById(
                subscriptionById.values().stream().map(SubscriptionEntity::getServiceId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(ServiceEntity::getId, s -> s));

        Map<Long, UserEntity> ownerById = userRepository.findAllById(
                subscriptionById.values().stream().map(SubscriptionEntity::getUserId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(UserEntity::getId, u -> u));

        for (SubscriptionUserEntity su : membership) {
            SubscriptionEntity sub = subscriptionById.get(su.getSubscriptionId());
            if (sub == null) {
                continue;
            }
            ServiceEntity service = serviceById.get(sub.getServiceId());
            UserEntity owner = ownerById.get(sub.getUserId());
            String ownerName = owner == null ? null : owner.getName();

            // Default permissions
            Map<Long, String> accessByPermission = new LinkedHashMap<>();
            for (ServiceRolePermissionEntity mapping : serviceRolePermissionRepository.findByRoleId(su.getRoleId())) {
                accessByPermission.put(mapping.getPermissionId(), "ALLOWED");
            }

            // Apply overrides
            if (su.getSpecialPermissions() != null) {
                for (SpecialPermissionEntity sp : su.getSpecialPermissions()) {
                    if (sp.getPermissionId() == null) {
                        continue;
                    }
                    String access = sp.getAccess() == null ? "" : sp.getAccess().toUpperCase(Locale.ROOT);
                    if ("DENIED".equals(access)) {
                        accessByPermission.put(sp.getPermissionId(), "DENIED");
                    } else if ("ALLOWED".equals(access)) {
                        accessByPermission.put(sp.getPermissionId(), "ALLOWED");
                    }
                }
            }

            if (accessByPermission.isEmpty()) {
                continue;
            }

            List<PermissionEntity> permissions = permissionRepository.findAllById(accessByPermission.keySet());
            Map<Long, String> permissionNameById = permissions.stream()
                    .collect(Collectors.toMap(PermissionEntity::getId, PermissionEntity::getName));

            for (Map.Entry<Long, String> entry : accessByPermission.entrySet()) {
                String permissionName = permissionNameById.get(entry.getKey());
                if (permissionName == null) {
                    continue;
                }
                result.add(new UserPermissionDTO(
                        sub.getId(),
                        ownerName,
                        service == null ? null : service.getName(),
                        permissionName,
                        entry.getValue()
                ));
            }
        }

        return result;
    }
}
