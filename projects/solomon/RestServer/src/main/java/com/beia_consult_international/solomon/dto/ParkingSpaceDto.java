package com.beia_consult_international.solomon.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class ParkingSpaceDto {
    private long id;
    private double latitude;
    private double longitude;
    private long mallId;
    private List<ParkingDataDto> parkingData;

    public ParkingSpaceDto() {
    }

    public ParkingSpaceDto(long id, double latitude, double longitude, long mallId, List<ParkingDataDto> parkingData) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mallId = mallId;
        this.parkingData = parkingData;
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

    public long getMall() {
        return mallId;
    }

    public void setMall(long mallId) {
        this.mallId = mallId;
    }

    public List<ParkingDataDto> getParkingData() {
        return parkingData;
    }

    public void setParkingData(List<ParkingDataDto> parkingData) {
        this.parkingData = parkingData;
    }
}
