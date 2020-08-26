package com.beia_consult_international.solomon.exception;

public class BeaconNotFoundException extends RuntimeException {
    public BeaconNotFoundException() {
        super("Beacon not found!");
    }
}
