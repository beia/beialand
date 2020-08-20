package com.beia_consult_international.solomon.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler({
        UserNotFoundException.class,
        WrongUserDetailsException.class
    })
    public ResponseEntity<String> handle(Exception e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}
