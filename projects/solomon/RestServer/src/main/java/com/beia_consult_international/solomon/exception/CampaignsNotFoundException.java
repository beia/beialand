package com.beia_consult_international.solomon.exception;

public class CampaignsNotFoundException extends RuntimeException {
    public CampaignsNotFoundException() {
        super("Campaigns not found!");
    }
}
