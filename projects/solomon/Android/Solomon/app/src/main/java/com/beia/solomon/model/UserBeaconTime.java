package com.beia.solomon.model;
import lombok.Builder;

@Builder
public class UserBeaconTime {
    private long id;
    private long seconds;
    private User user;
    private Beacon beacon;

    public UserBeaconTime() {
    }

    public UserBeaconTime(long id, long seconds, User user, Beacon beacon) {
        this.id = id;
        this.seconds = seconds;
        this.user = user;
        this.beacon = beacon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }
}
