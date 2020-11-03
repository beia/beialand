package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.ConversationDto;
import com.beia_consult_international.solomon.model.Conversation;

public abstract class ConversationMapper {

    public static ConversationDto maptoDto(Conversation model) {
        return ConversationDto
                .builder()
                .id(model.getId())
                .status(model.getStatus())
                .user1(UserMapper.mapToDto(model.getUser1(), "src/main/resources/users/"))
                .user2(UserMapper.mapToDto(model.getUser2(), "src/main/resources/users/"))
                .build();
    }

    public static Conversation mapToModel(ConversationDto dto) {
        return Conversation
                .builder()
                .id(dto.getId())
                .status(dto.getStatus())
                .user1(UserMapper.mapToModel(dto.getUser1()))
                .user2(UserMapper.mapToModel(dto.getUser2()))
                .build();
    }
}
