package com.beia_consult_international.solomon.exception;

public class ConversationAlreadyStartedException extends RuntimeException {
    public ConversationAlreadyStartedException() {
        super("Conversation already started!");
    }
}
