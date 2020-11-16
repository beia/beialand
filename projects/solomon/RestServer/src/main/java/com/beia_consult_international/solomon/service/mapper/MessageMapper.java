package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.MessageDto;
import com.beia_consult_international.solomon.exception.ConversationNotFoundException;
import com.beia_consult_international.solomon.exception.UserNotFoundException;
import com.beia_consult_international.solomon.model.Message;
import com.beia_consult_international.solomon.repository.ConversationRepository;
import com.beia_consult_international.solomon.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MessageMapper {
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    public MessageMapper(UserRepository userRepository, ConversationRepository conversationRepository) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
    }

    public MessageDto mapToDto(Message model) {
        return MessageDto
                .builder()
                .id(model.getId())
                .text(model.getText())
                .date(model.getDate().toString())
                .senderId(model.getSender().getId())
                .receiverId(model.getReceiver().getId())
                .conversationId(model.getConversation().getId())
                .build();
    }

    public Message mapToModel(MessageDto dto) {
        return Message
                .builder()
                .id(dto.getId())
                .text(dto.getText())
                .date(LocalDateTime.parse(dto.getDate()))
                .sender(userRepository
                        .findById(dto.getSenderId())
                        .orElseThrow(UserNotFoundException::new))
                .receiver(userRepository
                        .findById(dto.getReceiverId())
                        .orElseThrow(UserNotFoundException::new))
                .conversation(conversationRepository
                        .findById(dto.getConversationId())
                        .orElseThrow(ConversationNotFoundException::new))
                .build();
    }
}
