package com.beia_consult_international.solomon.exception;

public class ConversationNotFoundException extends RuntimeException {
    public ConversationNotFoundException() {
        super("Conversation not found!");
    }
}
