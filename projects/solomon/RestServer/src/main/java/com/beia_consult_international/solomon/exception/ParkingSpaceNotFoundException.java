package com.beia_consult_international.solomon.exception;

public class ParkingSpaceNotFoundException extends RuntimeException {
    public ParkingSpaceNotFoundException() {
        super("Parking space not found!");
    }
}
