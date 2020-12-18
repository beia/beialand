package com.beia.solomon_smart_shopping.model;

import java.util.List;

public class Conversation {
    private long id;
    private ConversationStatus status;
    private User user1;
    private User user2;
    private List<Message> messages;

    private Conversation(Builder builder) {
        this.id = builder.id;
        this.status = builder.status;
        this.user1 = builder.user1;
        this.user2 = builder.user2;
        this.messages = builder.messages;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public static class Builder {
        private long id;
        private ConversationStatus status;
        private User user1;
        private User user2;
        private List<Message> messages;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder status(ConversationStatus status) {
            this.status = status;
            return this;
        }

        public Builder user1(User user1) {
            this.user1 = user1;
            return this;
        }

        public Builder user2(User user2) {
            this.user2 = user2;
            return this;
        }

        public Builder messages(List<Message> messages) {
            this.messages = messages;
            return this;
        }

        public Conversation build() {
            return new Conversation(this);
        }
    }
}
