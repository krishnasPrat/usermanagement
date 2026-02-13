package com.pratishthan.usermanagement.controller;

import com.pratishthan.usermanagement.dto.SubscriptionUserDTO;
import com.pratishthan.usermanagement.service.SubscriptionUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscription-user")
public class SubscriptionUserController {

    private final SubscriptionUserService subscriptionUserService;

    public SubscriptionUserController(SubscriptionUserService subscriptionUserService) {
        this.subscriptionUserService = subscriptionUserService;
    }

    @PostMapping
    public ResponseEntity<SubscriptionUserDTO> createSubscriptionUser(@RequestBody SubscriptionUserDTO subscriptionUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionUserService.createSubscriptionUser(subscriptionUser));
    }
}
