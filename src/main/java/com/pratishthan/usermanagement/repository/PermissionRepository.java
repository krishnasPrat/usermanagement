package com.pratishthan.usermanagement.repository;

import com.pratishthan.usermanagement.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
}
