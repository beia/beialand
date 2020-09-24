package com.beia_consult_international.solomon.exception;

public class MallNotFoundException extends RuntimeException {
    public MallNotFoundException() {
        super("Mall not found!");
    }
}
