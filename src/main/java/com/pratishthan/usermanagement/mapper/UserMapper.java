package com.pratishthan.usermanagement.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratishthan.usermanagement.dto.UserDTO;
import com.pratishthan.usermanagement.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ObjectMapper objectMapper;

    public UserMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public UserEntity toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        return objectMapper.convertValue(dto, UserEntity.class);
    }

    public UserDTO toDTO(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return objectMapper.convertValue(entity, UserDTO.class);
    }
}
