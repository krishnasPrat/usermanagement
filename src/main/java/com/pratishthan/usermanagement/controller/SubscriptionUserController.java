package com.pratishthan.usermanagement.controller;

import com.pratishthan.usermanagement.dto.PermissionListDTO;
import com.pratishthan.usermanagement.dto.PermissionUpdateDTO;
import com.pratishthan.usermanagement.dto.SubscriptionUserDTO;
import com.pratishthan.usermanagement.service.SubscriptionUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/subscription/{subscriptionId}/user/{userId}/permissions")
    public ResponseEntity<PermissionListDTO> getPermissions(
            @PathVariable Long subscriptionId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(subscriptionUserService.getPermissionsForSubscriptionUser(subscriptionId, userId));
    }

    @PostMapping("/subscription/{subscriptionId}/user/{userId}/permissions/add")
    public ResponseEntity<SubscriptionUserDTO> addPermissions(
            @PathVariable Long subscriptionId,
            @PathVariable Long userId,
            @RequestBody PermissionUpdateDTO update
    ) {
        return ResponseEntity.ok(subscriptionUserService.addSpecialPermissions(subscriptionId, userId, update));
    }

    @PostMapping("/subscription/{subscriptionId}/user/{userId}/permissions/remove")
    public ResponseEntity<SubscriptionUserDTO> removePermissions(
            @PathVariable Long subscriptionId,
            @PathVariable Long userId,
            @RequestBody PermissionUpdateDTO update
    ) {
        return ResponseEntity.ok(subscriptionUserService.removeSpecialPermissions(subscriptionId, userId, update));
    }
}
