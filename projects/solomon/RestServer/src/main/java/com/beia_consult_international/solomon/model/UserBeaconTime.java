package com.beia_consult_international.solomon.model;

import com.sun.istack.NotNull;
import lombok.Builder;

import javax.persistence.*;

@Entity
@Builder
public class UserBeaconTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long seconds;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "beacon_id", nullable = false)
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
