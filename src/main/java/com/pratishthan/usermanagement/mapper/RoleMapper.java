package com.pratishthan.usermanagement.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratishthan.usermanagement.dto.RoleDTO;
import com.pratishthan.usermanagement.entity.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    private final ObjectMapper objectMapper;

    public RoleMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RoleEntity toEntity(RoleDTO dto) {
        if (dto == null) {
            return null;
        }
        return objectMapper.convertValue(dto, RoleEntity.class);
    }

    public RoleDTO toDTO(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        return objectMapper.convertValue(entity, RoleDTO.class);
    }
}
