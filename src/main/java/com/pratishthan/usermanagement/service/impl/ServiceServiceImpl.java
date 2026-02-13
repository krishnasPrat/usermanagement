package com.pratishthan.usermanagement.service.impl;

import com.pratishthan.usermanagement.dto.PermissionDTO;
import com.pratishthan.usermanagement.dto.RoleDTO;
import com.pratishthan.usermanagement.dto.RolePermissionMappingDTO;
import com.pratishthan.usermanagement.dto.ServiceDTO;
import com.pratishthan.usermanagement.entity.ServiceEntity;
import com.pratishthan.usermanagement.mapper.ServiceMapper;
import com.pratishthan.usermanagement.repository.ServiceRepository;
import com.pratishthan.usermanagement.service.RolePermissionService;
import com.pratishthan.usermanagement.service.ServiceService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final RolePermissionService rolePermissionService;
    private final ServiceMapper serviceMapper;

    public ServiceServiceImpl(ServiceRepository serviceRepository, RolePermissionService rolePermissionService, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.rolePermissionService = rolePermissionService;
        this.serviceMapper = serviceMapper;
    }

    @Override
    public ServiceDTO createService(ServiceDTO service) {
        ServiceEntity savedService = serviceRepository.save(serviceMapper.toEntity(service));

        List<RoleDTO> savedRoles = new ArrayList<>();
        Map<String, RoleDTO> rolesByName = new HashMap<>();
        if (service.roles() != null) {
            for (RoleDTO role : service.roles()) {
                persistRoles(role, savedService, savedRoles, rolesByName);
            }
        }

        List<PermissionDTO> savedPermissions = new ArrayList<>();
        Map<String, PermissionDTO> permissionsByName = new HashMap<>();
        if (service.permissions() != null) {
            persistPermissions(service, savedService, savedPermissions, permissionsByName);
        }

        List<RolePermissionMappingDTO> savedMappings = new ArrayList<>();
        if (service.rolePermissionMappings() != null) {
            for (RolePermissionMappingDTO mapping : service.rolePermissionMappings()) {
                persistRolePermissionMappings(mapping, rolesByName, permissionsByName, savedMappings);
            }
        }

        return serviceMapper.toDTO(savedService, savedRoles, savedPermissions, savedMappings);
    }

    private void persistPermissions(ServiceDTO service, ServiceEntity savedService, List<PermissionDTO> savedPermissions, Map<String, PermissionDTO> permissionsByName) {
        for (PermissionDTO permission : service.permissions()) {
            PermissionDTO permissionWithService = new PermissionDTO(
                    permission.id(),
                    savedService.getId(),
                    permission.name(),
                    permission.description()
            );
            PermissionDTO savedPermission = rolePermissionService.createPermission(permissionWithService);
            savedPermissions.add(savedPermission);
            if (savedPermission.name() != null) {
                permissionsByName.put(savedPermission.name(), savedPermission);
            }
        }
    }

    private void persistRoles(RoleDTO role, ServiceEntity savedService, List<RoleDTO> savedRoles, Map<String, RoleDTO> rolesByName) {
        RoleDTO roleWithService = new RoleDTO(
                role.id(),
                savedService.getId(),
                role.name(),
                role.description()
        );
        RoleDTO savedRole = rolePermissionService.createRole(roleWithService);
        savedRoles.add(savedRole);
        if (savedRole.name() != null) {
            rolesByName.put(savedRole.name(), savedRole);
        }
    }

    private void persistRolePermissionMappings(
            RolePermissionMappingDTO mapping,
            Map<String, RoleDTO> rolesByName,
            Map<String, PermissionDTO> permissionsByName,
            List<RolePermissionMappingDTO> savedMappings
    ) {
        Long roleId = 0L;
        Long permissionId = 0L;
        String roleName = mapping.roleName();
        String permissionName = mapping.permissionName();

        RoleDTO role = rolesByName.get(roleName);
        if (role != null) {
            roleId = role.id();
            roleName = role.name();
        }

        PermissionDTO permission = permissionsByName.get(permissionName);
        if (permission != null) {
            permissionId = permission.id();
            permissionName = permission.name();
        }


        RolePermissionMappingDTO toCreate = new RolePermissionMappingDTO(
                roleId,
                permissionId,
                roleName,
                permissionName
        );

        RolePermissionMappingDTO savedMapping = rolePermissionService.createRolePermissionMapping(toCreate);
        savedMappings.add(savedMapping);
    }

}
