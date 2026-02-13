package com.pratishthan.usermanagement.service.impl;

import com.pratishthan.usermanagement.dto.PermissionDTO;
import com.pratishthan.usermanagement.dto.RoleDTO;
import com.pratishthan.usermanagement.dto.RolePermissionMappingDTO;
import com.pratishthan.usermanagement.entity.PermissionEntity;
import com.pratishthan.usermanagement.entity.RoleEntity;
import com.pratishthan.usermanagement.entity.ServiceRolePermissionEntity;
import com.pratishthan.usermanagement.mapper.PermissionMapper;
import com.pratishthan.usermanagement.mapper.RoleMapper;
import com.pratishthan.usermanagement.repository.PermissionRepository;
import com.pratishthan.usermanagement.repository.RoleRepository;
import com.pratishthan.usermanagement.repository.ServiceRolePermissionRepository;
import com.pratishthan.usermanagement.service.RolePermissionService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    public RolePermissionServiceImpl(
            RoleRepository roleRepository, PermissionRepository permissionRepository,
                                     ServiceRolePermissionRepository serviceRolePermissionRepository, RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.serviceRolePermissionRepository = serviceRolePermissionRepository;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ServiceRolePermissionRepository serviceRolePermissionRepository;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public RoleDTO createRole(RoleDTO role) {
        RoleEntity entity = roleMapper.toEntity(role);
        RoleEntity saved = roleRepository.save(entity);
        return roleMapper.toDTO(saved);
    }

    @Override
    public PermissionDTO createPermission(PermissionDTO permission) {
        PermissionEntity entity = permissionMapper.toEntity(permission);
        PermissionEntity saved = permissionRepository.save(entity);
        return permissionMapper.toDTO(saved);
    }

    @Override
    public RolePermissionMappingDTO createRolePermissionMapping(RolePermissionMappingDTO mapping) {
        if (mapping == null || mapping.roleId() == null || mapping.permissionId() == null) {
            throw new IllegalArgumentException("roleId and permissionId are required");
        }

        RoleEntity role = roleRepository.findById(mapping.roleId())
                .orElseThrow(() -> new IllegalArgumentException("RoleEntity not found"));
        PermissionEntity permission = permissionRepository.findById(mapping.permissionId())
                .orElseThrow(() -> new IllegalArgumentException("PermissionEntity not found"));

        if (!role.getServiceId().equals(permission.getServiceId())) {
            throw new IllegalArgumentException("RoleEntity and permission must belong to the same service");
        }

        ServiceRolePermissionEntity entity = new ServiceRolePermissionEntity();
        entity.setServiceId(role.getServiceId());
        entity.setRoleId(role.getId());
        entity.setPermissionId(permission.getId());

        ServiceRolePermissionEntity saved = serviceRolePermissionRepository.save(entity);

        return new RolePermissionMappingDTO(
                saved.getRoleId(),
                saved.getPermissionId(),
                role.getName(),
                permission.getName()
        );
    }
}
