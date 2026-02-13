package com.pratishthan.usermanagement.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratishthan.usermanagement.dto.PermissionDTO;
import com.pratishthan.usermanagement.entity.PermissionEntity;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    private final ObjectMapper objectMapper;

    public PermissionMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public PermissionEntity toEntity(PermissionDTO dto) {
        if (dto == null) {
            return null;
        }
        return objectMapper.convertValue(dto, PermissionEntity.class);
    }

    public PermissionDTO toDTO(PermissionEntity entity) {
        if (entity == null) {
            return null;
        }
        return objectMapper.convertValue(entity, PermissionDTO.class);
    }
}
