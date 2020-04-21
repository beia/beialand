package com.beia.solomon.networkPackets;

import java.io.Serializable;

public class BeaconTime implements Serializable {
    private int idUser;
    private Beacon beacon;
    private long timeSeconds;

    public BeaconTime(int idUser, Beacon beacon, long timeSeconds) {
        this.idUser = idUser;
        this.beacon = beacon;
        this.timeSeconds = timeSeconds;
    }

    public int getIdUser() {
        return this.idUser;
    }
    public Beacon getBeacon() {
        return this.beacon;
    }
    public long getTimeSeconds() {
        return this.timeSeconds;
    }
    public void setTimeSeconds(long timeSeconds) {
        this.timeSeconds = timeSeconds;
    }
}