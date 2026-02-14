package com.pratishthan.usermanagement.repository;

import com.pratishthan.usermanagement.entity.SubscriptionUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionUserRepository extends JpaRepository<SubscriptionUserEntity, Long> {
    Optional<SubscriptionUserEntity> findBySubscriptionIdAndUserId(Long subscriptionId, Long userId);
}
