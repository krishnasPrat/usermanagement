package com.pratishthan.usermanagement.mapper;

import com.pratishthan.usermanagement.dto.ServiceDTO;
import com.pratishthan.usermanagement.entity.ServiceEntity;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceEntity toEntity(ServiceDTO dto) {
        if (dto == null) {
            return null;
        }
        ServiceEntity entity = new ServiceEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setStatus(dto.status());
        return entity;
    }

    public ServiceDTO toDTO(ServiceEntity entity, java.util.List<com.pratishthan.usermanagement.dto.RoleDTO> roles, java.util.List<com.pratishthan.usermanagement.dto.PermissionDTO> permissions, java.util.List<com.pratishthan.usermanagement.dto.RolePermissionMappingDTO> mappings) {
        if (entity == null) {
            return null;
        }
        return new ServiceDTO(
                entity.getId(),
                entity.getName(),
                entity.getStatus(),
                roles,
                permissions,
                mappings
        );
    }
}
