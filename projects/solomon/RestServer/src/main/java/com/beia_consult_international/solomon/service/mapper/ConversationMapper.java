package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.ConversationDto;
import com.beia_consult_international.solomon.model.Conversation;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ConversationMapper {
    MessageMapper messageMapper;

    public ConversationMapper(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public ConversationDto maptoDto(Conversation model) {
        return ConversationDto
                .builder()
                .id(model.getId())
                .status(model.getStatus())
                .user1(UserMapper.mapToDto(model.getUser1(), "src/main/resources/users/"))
                .user2(UserMapper.mapToDto(model.getUser2(), "src/main/resources/users/"))
                .messages(model
                        .getMessages()
                        .stream()
                        .map(message -> messageMapper.mapToDto(message))
                        .collect(Collectors.toList()))
                .build();
    }

    public Conversation mapToModel(ConversationDto dto) {
        return Conversation
                .builder()
                .id(dto.getId())
                .status(dto.getStatus())
                .user1(UserMapper.mapToModel(dto.getUser1()))
                .user2(UserMapper.mapToModel(dto.getUser2()))
                .messages(dto
                        .getMessages()
                        .stream()
                        .map(messageDto -> messageMapper.mapToModel(messageDto))
                        .collect(Collectors.toList()))
                .build();
    }
}
