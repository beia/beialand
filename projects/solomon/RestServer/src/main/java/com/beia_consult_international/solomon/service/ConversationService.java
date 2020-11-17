package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.ConversationDto;
import com.beia_consult_international.solomon.dto.MessageDto;
import com.beia_consult_international.solomon.exception.ConversationAlreadyStartedException;
import com.beia_consult_international.solomon.exception.ConversationNotFoundException;
import com.beia_consult_international.solomon.exception.UserNotFoundException;
import com.beia_consult_international.solomon.model.*;
import com.beia_consult_international.solomon.repository.ConversationRepository;
import com.beia_consult_international.solomon.repository.MessageRepository;
import com.beia_consult_international.solomon.repository.UserRepository;
import com.beia_consult_international.solomon.service.mapper.ConversationMapper;
import com.beia_consult_international.solomon.service.mapper.MessageMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConversationService {
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;


    public ConversationService(UserRepository userRepository, ConversationRepository conversationRepository, ConversationMapper conversationMapper, MessageRepository messageRepository, MessageMapper messageMapper) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.conversationMapper = conversationMapper;
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    public void sendChatNotificationsToAllAgents(long userId) throws FirebaseMessagingException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message
                .builder()
                .putData("messageType", FcmMessageType.AGENT_REQUEST.name())
                .putData("title", user.getUsername() + " wants to chat with you...")
                .putData("message", "Click to respond")
                .putData("userId", Long.toString(userId))
                .setTopic(Topic.AGENT.name())
                .build();
        FirebaseMessaging.getInstance().send(message);
    }

    public ConversationDto startConversation(long agentId, long userId) throws FirebaseMessagingException {
        User agent = userRepository
                .findById(agentId)
                .orElseThrow(UserNotFoundException::new);
        User user = userRepository
                .findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Conversation conversation = Conversation
                .builder()
                .status(ConversationStatus.STARTED)
                .user1(agent)
                .user2(user)
                .messages(new ArrayList<>())
                .build();
        List<Conversation> previousConversations = conversationRepository
                .findByUser2(conversation.getUser2())
                .orElse(new ArrayList<>())
                .stream()
                .filter(c -> c.getStatus().equals(ConversationStatus.STARTED))
                .collect(Collectors.toList());
        if (previousConversations.isEmpty()) {
            conversation.setMessages(List.of(Message
                    .builder()
                    .sender(conversation.getUser1())
                    .receiver(conversation.getUser2())
                    .text("Hello " + conversation.getUser2().getFirstName() + ", how can I help you?")
                    .date(LocalDateTime.now())
                    .conversation(conversation)
                    .build()));
            conversationRepository.save(conversation);
            com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message
                    .builder()
                    .putData("messageType", FcmMessageType.CONVERSATION.name())
                    .putData("conversationId", Long.toString(conversation.getId()))
                    .setToken(conversation.getUser2().getFcmToken())
                    .build();
            FirebaseMessaging.getInstance().send(message);
            return conversationMapper.maptoDto(conversation);
        } else {
            throw new ConversationAlreadyStartedException();
        }
    }

    public ConversationDto findById(long id) {
        return conversationMapper.maptoDto(conversationRepository
                .findById(id)
                .orElseThrow(ConversationNotFoundException::new));
    }

    public List<MessageDto> findMessagesByConversationId(long id) {
        return conversationRepository
                .findById(id)
                .orElseThrow(ConversationNotFoundException::new)
                .getMessages()
                .stream()
                .map(messageMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public MessageDto saveMessage(MessageDto messageDto) {
        Message message = messageMapper.mapToModel(messageDto);
        Conversation conversation = conversationRepository
                .findById(messageDto.getConversationId())
                .orElseThrow(ConversationNotFoundException::new);
        conversation
                .getMessages()
                .add(message);
        conversationRepository.save(conversation);
        return messageMapper.mapToDto(message);
    }
}
