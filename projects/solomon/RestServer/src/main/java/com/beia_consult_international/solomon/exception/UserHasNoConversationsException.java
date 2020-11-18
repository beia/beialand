package com.beia_consult_international.solomon.exception;

public class UserHasNoConversationsException extends RuntimeException {
    public UserHasNoConversationsException() {
        super("User has no conversations!");
    }
}
