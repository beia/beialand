package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.UserDto;
import com.beia_consult_international.solomon.exception.UserNotFoundException;
import com.beia_consult_international.solomon.model.User;
import com.beia_consult_international.solomon.repository.UserRepository;
import com.beia_consult_international.solomon.service.mapper.UserMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Boolean validUserDetails(String username, String password) {
        User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        return passwordEncoder.matches(password, user.getPassword());
    }

    public Boolean userExists(UserDto userDto) {
        User user = UserMapper.mapToModel(userDto);
        try { findUserByUsername(user.getUsername()); }
        catch (UserNotFoundException e) { return false; }
        return true;
    }

    public UserDto findUserByUsername(String username) {
        return userRepository
                .findUserByUsername(username)
                .map(UserMapper::mapToDto)
                .orElseThrow(UserNotFoundException::new);
    }

    public void save(UserDto userDto, String password) {
        User user = UserMapper.mapToModel(userDto);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
