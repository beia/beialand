package com.beia_consult_international.solomon;

import com.beia_consult_international.solomon.model.Role;
import com.beia_consult_international.solomon.model.User;
import com.beia_consult_international.solomon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SolomonApplication implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SolomonApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("solomon"))
                .firstName("Admin")
                .role(Role.ADMIN)
                .build();
        userRepository.save(user);
    }
}
