package com.beia.solomon.model;

import lombok.Builder;

@Builder
public class Message {
    private long id;
    private String text;
    private long senderId;
    private long receiverId;
    private long conversationId;

    public Message() {
    }

    public Message(long id, String text, long senderId, long receiverId, long conversationId) {
        this.id = id;
        this.text = text;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.conversationId = conversationId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }
}
