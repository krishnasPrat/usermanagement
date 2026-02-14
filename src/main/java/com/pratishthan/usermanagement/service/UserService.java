package com.pratishthan.usermanagement.service;

import com.pratishthan.usermanagement.dto.UserDTO;
import com.pratishthan.usermanagement.dto.UserPermissionDTO;
import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO user);
    List<UserPermissionDTO> getUserPermissions(Long userId);
}
