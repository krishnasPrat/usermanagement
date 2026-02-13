package com.pratishthan.usermanagement.service.impl;

import com.pratishthan.usermanagement.dto.SubscriptionDTO;
import com.pratishthan.usermanagement.mapper.SubscriptionMapper;
import com.pratishthan.usermanagement.repository.ServiceRepository;
import com.pratishthan.usermanagement.repository.SubscriptionRepository;
import com.pratishthan.usermanagement.repository.UserRepository;
import com.pratishthan.usermanagement.service.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository, ServiceRepository serviceRepository, SubscriptionMapper subscriptionMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.subscriptionMapper = subscriptionMapper;
    }

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO subscription) {
        if (subscription == null || subscription.userId() == null || subscription.serviceId() == null) {
            throw new IllegalArgumentException("userId and serviceId are required");
        }
        if (!userRepository.existsById(subscription.userId())) {
            throw new IllegalArgumentException("UserEntity not found");
        }
        if (!serviceRepository.existsById(subscription.serviceId())) {
            throw new IllegalArgumentException("ServiceEntity not found");
        }
        var entity = subscriptionMapper.toEntity(subscription);
        var saved = subscriptionRepository.save(entity);
        return subscriptionMapper.toDTO(saved);
    }
}
