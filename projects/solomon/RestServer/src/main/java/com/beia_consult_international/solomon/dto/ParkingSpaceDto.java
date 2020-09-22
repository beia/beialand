package com.beia_consult_international.solomon.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class ParkingSpaceDto {
    private long id;
    private double latitude;
    private double longitude;
    private float rotation;
    private long mallId;
    private List<ParkingDataDto> parkingData;
    private BeaconDto beacon;

    public ParkingSpaceDto() {
    }

    public ParkingSpaceDto(long id, double latitude, double longitude, float rotation, long mallId, List<ParkingDataDto> parkingData, BeaconDto beacon) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rotation = rotation;
        this.mallId = mallId;
        this.parkingData = parkingData;
        this.beacon = beacon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public long getMallId() {
        return mallId;
    }

    public void setMallId(long mallId) {
        this.mallId = mallId;
    }

    public List<ParkingDataDto> getParkingData() {
        return parkingData;
    }

    public void setParkingData(List<ParkingDataDto> parkingData) {
        this.parkingData = parkingData;
    }

    public BeaconDto getBeacon() {
        return beacon;
    }

    public void setBeacon(BeaconDto beacon) {
        this.beacon = beacon;
    }
}
