package com.beia_consult_international.solomon.dto;

import com.beia_consult_international.solomon.model.ConversationStatus;
import com.beia_consult_international.solomon.model.User;
import lombok.Builder;

@Builder
public class ConversationDto {
    private long id;
    private ConversationStatus status;
    private UserDto user1;
    private UserDto user2;

    public ConversationDto() {
    }

    public ConversationDto(long id, ConversationStatus status, UserDto user1, UserDto user2) {
        this.id = id;
        this.status = status;
        this.user1 = user1;
        this.user2 = user2;
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

    public UserDto getUser1() {
        return user1;
    }

    public void setUser1(UserDto user1) {
        this.user1 = user1;
    }

    public UserDto getUser2() {
        return user2;
    }

    public void setUser2(UserDto user2) {
        this.user2 = user2;
    }
}
