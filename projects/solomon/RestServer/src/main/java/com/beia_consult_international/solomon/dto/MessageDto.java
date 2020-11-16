package com.beia_consult_international.solomon.dto;

import lombok.Builder;

@Builder
public class MessageDto {
    private long id;
    private String text;
    private String date;
    private long senderId;
    private long receiverId;
    private long conversationId;

    public MessageDto() {
    }

    public MessageDto(long id, String text, String date, long senderId, long receiverId, long conversationId) {
        this.id = id;
        this.text = text;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
