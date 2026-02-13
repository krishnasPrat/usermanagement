package com.pratishthan.usermanagement.repository;

import com.pratishthan.usermanagement.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
