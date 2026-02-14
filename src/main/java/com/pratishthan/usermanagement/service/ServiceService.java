package com.pratishthan.usermanagement.service;

import com.pratishthan.usermanagement.dto.ServiceDTO;
import java.util.List;

public interface ServiceService {
    ServiceDTO createService(ServiceDTO service);
    List<ServiceDTO> findByName(String name);
}
