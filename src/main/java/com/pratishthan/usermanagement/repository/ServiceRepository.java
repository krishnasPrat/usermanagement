package com.pratishthan.usermanagement.repository;

import com.pratishthan.usermanagement.entity.ServiceEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByName(String name);
}
