package com.beia_consult_international.solomon.controller;

import com.beia_consult_international.solomon.dto.UserDto;
import com.beia_consult_international.solomon.model.User;
import com.beia_consult_international.solomon.service.UserService;
import com.beia_consult_international.solomon.service.mapper.UserMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void save(@RequestBody UserDto user) {
        userService.save(UserMapper.mapToModel(user));
    }
}
