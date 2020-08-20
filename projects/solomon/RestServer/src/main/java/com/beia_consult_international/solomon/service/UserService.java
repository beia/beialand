package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.UserDto;
import com.beia_consult_international.solomon.exception.UserNotFoundException;
import com.beia_consult_international.solomon.model.User;
import com.beia_consult_international.solomon.repository.UserRepository;
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
        User user = findUserByUsername(username);
        return passwordEncoder.matches(password, user.getPassword());
    }

    public Boolean userExists(User user) {
        try { findUserByUsername(user.getUsername()); }
        catch (UserNotFoundException e) { return false; }
        return true;
    }

    public User findUserByUsername(String username) {
        return userRepository
                .findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    public void save(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
