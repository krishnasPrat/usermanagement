package com.pratishthan.usermanagement.repository;

import com.pratishthan.usermanagement.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
