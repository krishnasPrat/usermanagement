package com.pratishthan.usermanagement.service.impl;

import com.pratishthan.usermanagement.dto.PermissionDTO;
import com.pratishthan.usermanagement.dto.PermissionListDTO;
import com.pratishthan.usermanagement.dto.SpecialPermissionDTO;
import com.pratishthan.usermanagement.dto.SubscriptionUserDTO;
import com.pratishthan.usermanagement.entity.PermissionEntity;
import com.pratishthan.usermanagement.entity.RoleEntity;
import com.pratishthan.usermanagement.entity.ServiceRolePermissionEntity;
import com.pratishthan.usermanagement.entity.SpecialPermissionEntity;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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

        Set<Long> defaultPermissions = loadDefaultPermissions(role.getId());

        SubscriptionUserEntity entity = new SubscriptionUserEntity();
        entity.setSubscriptionId(subscriptionUser.subscriptionId());
        entity.setUserId(subscriptionUser.userId());
        entity.setRoleId(subscriptionUser.roleId());
        entity.setStatus(subscriptionUser.status());

        List<SpecialPermissionEntity> current = entity.getSpecialPermissions();
        if (current == null) {
            current = new ArrayList<>();
            entity.setSpecialPermissions(current);
        }
        applySpecialPermissionUpserts(subscription, defaultPermissions, current, subscriptionUser.specialPermissions());

        SubscriptionUserEntity saved = subscriptionUserRepository.save(entity);

        return toDTO(saved);
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

        if (subscriptionUser.getSpecialPermissions() != null) {
            for (SpecialPermissionEntity sp : subscriptionUser.getSpecialPermissions()) {
                if (sp.getPermissionId() == null) {
                    continue;
                }
                String access = sp.getAccess() == null ? "" : sp.getAccess().toUpperCase(Locale.ROOT);
                if ("DENIED".equals(access)) {
                    permissionIds.remove(sp.getPermissionId());
                } else if ("ALLOWED".equals(access)) {
                    permissionIds.add(sp.getPermissionId());
                }
            }
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

    @Override
    public SubscriptionUserDTO upsertSpecialPermissions(Long subscriptionId, Long userId, List<SpecialPermissionDTO> permissions) {
        SubscriptionUserEntity subscriptionUser = subscriptionUserRepository
                .findBySubscriptionIdAndUserId(subscriptionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("SubscriptionUserEntity not found"));

        SubscriptionEntity subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("SubscriptionEntity not found"));

        RoleEntity role = roleRepository.findById(subscriptionUser.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("RoleEntity not found"));

        Set<Long> defaultPermissions = loadDefaultPermissions(role.getId());

        List<SpecialPermissionEntity> current = subscriptionUser.getSpecialPermissions();
        if (current == null) {
            current = new ArrayList<>();
            subscriptionUser.setSpecialPermissions(current);
        }

        applySpecialPermissionUpserts(subscription, defaultPermissions, current, permissions);

        SubscriptionUserEntity saved = subscriptionUserRepository.save(subscriptionUser);
        return toDTO(saved);
    }

    private Set<Long> loadDefaultPermissions(Long roleId) {
        Set<Long> defaults = new HashSet<>();
        for (ServiceRolePermissionEntity mapping : serviceRolePermissionRepository.findByRoleId(roleId)) {
            defaults.add(mapping.getPermissionId());
        }
        return defaults;
    }

    private void applySpecialPermissionUpserts(
            SubscriptionEntity subscription,
            Set<Long> defaultPermissions,
            List<SpecialPermissionEntity> current,
            List<SpecialPermissionDTO> incoming
    ) {
        for (SpecialPermissionDTO dto : incoming == null ? List.<SpecialPermissionDTO>of() : incoming) {
            if (dto == null || dto.permissionId() == null || dto.access() == null) {
                continue;
            }

            PermissionEntity permission = permissionRepository.findById(dto.permissionId())
                    .orElseThrow(() -> new IllegalArgumentException("PermissionEntity not found"));
            if (!permission.getServiceId().equals(subscription.getServiceId())) {
                throw new IllegalArgumentException("PermissionEntity does not belong to the subscription service");
            }

            String access = dto.access().toUpperCase(Locale.ROOT);
            boolean isDefault = defaultPermissions.contains(permission.getId());

            SpecialPermissionEntity existing = current.stream()
                    .filter(sp -> permission.getId().equals(sp.getPermissionId()))
                    .findFirst()
                    .orElse(null);

            if (isDefault) {
                handleDefaultPermissionUpsert(current, access, existing, permission);
            } else {
                handleNonDefaultPermissionUpsert(current, access, existing, permission);
            }
        }
    }

    private static void handleDefaultPermissionUpsert(
            List<SpecialPermissionEntity> current,
            String access,
            SpecialPermissionEntity existing,
            PermissionEntity permission
    ) {
        if ("DENIED".equals(access)) {
            if (existing == null) {
                SpecialPermissionEntity sp = new SpecialPermissionEntity();
                sp.setPermissionId(permission.getId());
                sp.setAccess("DENIED");
                current.add(sp);
            }
        } else if ("ALLOWED".equals(access)) {
            if (existing != null) {
                current.remove(existing);
            }
        } else {
            throw new IllegalArgumentException("Invalid access: " + access);
        }
    }

    private static void handleNonDefaultPermissionUpsert(
            List<SpecialPermissionEntity> current,
            String access,
            SpecialPermissionEntity existing,
            PermissionEntity permission
    ) {
        if ("ALLOWED".equals(access)) {
            if (existing == null) {
                SpecialPermissionEntity sp = new SpecialPermissionEntity();
                sp.setPermissionId(permission.getId());
                sp.setAccess("ALLOWED");
                current.add(sp);
            }
        } else if ("DENIED".equals(access)) {
            if (existing != null) {
                current.remove(existing);
            }
        } else {
            throw new IllegalArgumentException("Invalid access: " + access);
        }
    }

    private SubscriptionUserDTO toDTO(SubscriptionUserEntity saved) {
        List<SpecialPermissionDTO> specials = new ArrayList<>();
        if (saved.getSpecialPermissions() != null) {
            for (SpecialPermissionEntity sp : saved.getSpecialPermissions()) {
                specials.add(new SpecialPermissionDTO(sp.getPermissionId(), sp.getAccess()));
            }
        }
        return new SubscriptionUserDTO(
                saved.getId(),
                saved.getSubscriptionId(),
                saved.getUserId(),
                saved.getRoleId(),
                saved.getStatus(),
                specials
        );
    }
}
