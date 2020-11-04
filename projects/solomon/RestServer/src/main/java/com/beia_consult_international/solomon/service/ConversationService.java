package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.ConversationDto;
import com.beia_consult_international.solomon.dto.MessageDto;
import com.beia_consult_international.solomon.exception.ConversationAlreadyStartedException;
import com.beia_consult_international.solomon.exception.ConversationNotFoundException;
import com.beia_consult_international.solomon.model.Conversation;
import com.beia_consult_international.solomon.model.ConversationStatus;
import com.beia_consult_international.solomon.model.FcmMessageType;
import com.beia_consult_international.solomon.model.Message;
import com.beia_consult_international.solomon.repository.ConversationRepository;
import com.beia_consult_international.solomon.repository.MessageRepository;
import com.beia_consult_international.solomon.service.mapper.ConversationMapper;
import com.beia_consult_international.solomon.service.mapper.MessageMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final ConversationMapper conversationMapper;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;


    public ConversationService(ConversationRepository conversationRepository, ConversationMapper conversationMapper, MessageRepository messageRepository, MessageMapper messageMapper) {
        this.conversationRepository = conversationRepository;
        this.conversationMapper = conversationMapper;
        this.messageRepository = messageRepository;
        this.messageMapper = messageMapper;
    }

    public ConversationDto startConversation(ConversationDto conversationDto) throws FirebaseMessagingException {
        Conversation conversation = conversationMapper.mapToModel(conversationDto);
        List<Conversation> previousConversations = conversationRepository
                .findByUser1AndUser2(conversation.getUser1(), conversation.getUser2())
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
                    .conversation(conversation)
                    .build()));
            conversationRepository.save(conversation);
            com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message
                    .builder()
                    .putData("messageType", FcmMessageType.MESSAGE.name())
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
        return findById(id).getMessages();
    }
}
