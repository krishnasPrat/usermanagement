package com.pratishthan.usermanagement.repository;

import com.pratishthan.usermanagement.entity.SubscriptionUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionUserRepository extends JpaRepository<SubscriptionUserEntity, Long> {
}
