package com.pratishthan.usermanagement.repository;

import com.pratishthan.usermanagement.entity.SubscriptionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    List<SubscriptionEntity> findByUserId(Long userId);
}
