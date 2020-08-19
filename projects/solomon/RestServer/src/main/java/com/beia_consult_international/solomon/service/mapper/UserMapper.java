package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.UserDto;
import com.beia_consult_international.solomon.model.User;

public abstract class UserMapper {

    public static UserDto mapToDto(User user) {
        return UserDto
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .age(user.getAge())
                .role(user.getRole().name())
                .imageUrl(user.getPictureUrl())
                .build();
    }

    public static User mapToModel(UserDto userDto) {
        return User
                .builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .gender(userDto.getGender())
                .age(userDto.getAge())
                .build();
    }
}
