package com.beia_consult_international.solomon.dto;

import com.beia_consult_international.solomon.model.Beacon;
import com.beia_consult_international.solomon.model.User;
import lombok.Builder;

@Builder
public class UserBeaconTimeDto {
    private long id;
    private long seconds;
    private UserDto user;
    private BeaconDto beacon;

    public UserBeaconTimeDto() {
    }

    public UserBeaconTimeDto(long id, long seconds, UserDto user, BeaconDto beacon) {
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

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public BeaconDto getBeacon() {
        return beacon;
    }

    public void setBeacon(BeaconDto beacon) {
        this.beacon = beacon;
    }
}
