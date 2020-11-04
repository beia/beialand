package com.beia_consult_international.solomon.exception;

import com.beia_consult_international.solomon.model.ConversationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler({
        UserNotFoundException.class,
        WrongUserDetailsException.class,
        BeaconNotFoundException.class,
        CampaignsNotFoundException.class,
        MallNotFoundException.class,
        ParkingSpaceNotFoundException.class,
        ConversationNotFoundException.class,
        ConversationAlreadyStartedException.class
    })
    public ResponseEntity<String> handle(Exception e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}
