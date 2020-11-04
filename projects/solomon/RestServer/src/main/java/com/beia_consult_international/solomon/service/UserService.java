package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.ConversationDto;
import com.beia_consult_international.solomon.dto.UserDto;
import com.beia_consult_international.solomon.exception.ConversationNotFoundException;
import com.beia_consult_international.solomon.exception.UserNotFoundException;
import com.beia_consult_international.solomon.model.*;
import com.beia_consult_international.solomon.repository.ConversationRepository;
import com.beia_consult_international.solomon.repository.UserRepository;
import com.beia_consult_international.solomon.service.mapper.ConversationMapper;
import com.beia_consult_international.solomon.service.mapper.UserMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;
    @Value("${solomon.usersPicturesPath}")
    public String usersPath;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ConversationRepository conversationRepository, ConversationMapper conversationMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.conversationRepository = conversationRepository;
        this.conversationMapper = conversationMapper;
    }

    public Boolean validUserDetails(String username, String password) {
        User user = userRepository
                .findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        return passwordEncoder.matches(password, user.getPassword());
    }

    public Boolean userExists(UserDto userDto) {
        try { findUserByUsername(userDto.getUsername()); }
        catch (UserNotFoundException e) { return false; }
        return true;
    }

    public UserDto findById(Long id) {
        return UserMapper.mapToDto(
                userRepository
                .findById(id)
                .orElseThrow(UserNotFoundException::new),
                usersPath);
    }

    public UserDto findUserByUsername(String username) {
        return UserMapper.mapToDto(userRepository
                .findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new),
                usersPath);
    }

    public void save(UserDto userDto, String password) throws IOException {
        User user = UserMapper.mapToModel(userDto);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public void savePicture(byte[] image, String path, long id) throws IOException {
        FileUtils.writeByteArrayToFile(new File(path + id + ".png"), image);
    }

    public void saveToken(long userId, String token) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(UserNotFoundException::new);
        user.setFcmToken(token.substring(1, token.length() - 1));
        userRepository.save(user);
    }

    public void sendChatNotificationsToAllAgents(long userId) throws FirebaseMessagingException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Message message = Message
                .builder()
                .putData("messageType", FcmMessageType.AGENT_REQUEST.name())
                .putData("title", user.getUsername() + " wants to chat with you...")
                .putData("message", "Click to respond")
                .putData("userId", Long.toString(userId))
                .setTopic(Topic.AGENT.name())
                .build();
        FirebaseMessaging.getInstance().send(message);
    }
}
