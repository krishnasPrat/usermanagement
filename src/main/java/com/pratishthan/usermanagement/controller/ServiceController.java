package com.pratishthan.usermanagement.controller;

import com.pratishthan.usermanagement.dto.ServiceDTO;
import com.pratishthan.usermanagement.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping
    public ResponseEntity<ServiceDTO> createService(@RequestBody ServiceDTO service) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceService.createService(service));
    }
}
