package com.pratishthan.usermanagement.service;

import com.pratishthan.usermanagement.dto.PermissionDTO;
import com.pratishthan.usermanagement.dto.RoleDTO;
import com.pratishthan.usermanagement.dto.RolePermissionMappingDTO;

public interface RolePermissionService {
    RoleDTO createRole(RoleDTO role);
    PermissionDTO createPermission(PermissionDTO permission);
    RolePermissionMappingDTO createRolePermissionMapping(RolePermissionMappingDTO mapping);
}
