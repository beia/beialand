package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.UserDto;
import com.beia_consult_international.solomon.model.Gender;
import com.beia_consult_international.solomon.model.Role;
import com.beia_consult_international.solomon.model.User;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

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
                .build();
    }

    public static UserDto mapToDto(User user, String usersPicturesPath) {
        UserDto userDto = UserDto
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .age(user.getAge())
                .role(user.getRole().name())
                .build();
        try {
            byte[] image = FileUtils.readFileToByteArray(new File(usersPicturesPath + user.getId() + ".png"));
            String encodedImage = Base64.getEncoder().encodeToString(image);
            userDto.setImage(encodedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userDto;
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
                .role(userDto.getRole().equals("USER") ? Role.USER : Role.ADMIN)
                .build();
    }
}
