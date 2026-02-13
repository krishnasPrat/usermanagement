package com.pratishthan.usermanagement.repository;

import com.pratishthan.usermanagement.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
}
