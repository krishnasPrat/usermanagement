package com.pratishthan.usermanagement.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pratishthan.usermanagement.dto.SubscriptionDTO;
import com.pratishthan.usermanagement.entity.SubscriptionEntity;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    private final ObjectMapper objectMapper;

    public SubscriptionMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public SubscriptionEntity toEntity(SubscriptionDTO dto) {
        if (dto == null) {
            return null;
        }
        return objectMapper.convertValue(dto, SubscriptionEntity.class);
    }

    public SubscriptionDTO toDTO(SubscriptionEntity entity) {
        if (entity == null) {
            return null;
        }
        return objectMapper.convertValue(entity, SubscriptionDTO.class);
    }
}
