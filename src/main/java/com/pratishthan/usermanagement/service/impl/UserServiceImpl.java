package com.pratishthan.usermanagement.service.impl;

import com.pratishthan.usermanagement.dto.UserDTO;
import com.pratishthan.usermanagement.mapper.UserMapper;
import com.pratishthan.usermanagement.repository.UserRepository;
import com.pratishthan.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO user) {
        var entity = userMapper.toEntity(user);
        var saved = userRepository.save(entity);
        return userMapper.toDTO(saved);
    }
}
