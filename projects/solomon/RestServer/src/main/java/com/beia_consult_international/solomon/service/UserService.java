package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.exception.UserNotFoundException;
import com.beia_consult_international.solomon.model.User;
import com.beia_consult_international.solomon.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User findUserByUsername(String username) {
        return userRepository
                .findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
