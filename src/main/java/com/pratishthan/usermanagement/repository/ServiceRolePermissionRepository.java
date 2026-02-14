package com.pratishthan.usermanagement.repository;

import com.pratishthan.usermanagement.entity.ServiceRolePermissionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRolePermissionRepository extends JpaRepository<ServiceRolePermissionEntity, Long> {
    List<ServiceRolePermissionEntity> findByRoleId(Long roleId);
}
