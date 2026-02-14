package com.pratishthan.usermanagement.controller;

import com.pratishthan.usermanagement.dto.UserDTO;
import com.pratishthan.usermanagement.dto.UserPermissionDTO;
import com.pratishthan.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
    }

    @GetMapping("/{userId}/permissions")
    public ResponseEntity<List<UserPermissionDTO>> getUserPermissions(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserPermissions(userId));
    }
}
