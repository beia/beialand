package com.beia_consult_international.solomon.exception;

public class WrongUserDetailsException extends RuntimeException {
    public WrongUserDetailsException() {
        super("Wrong user details!");
    }
}
