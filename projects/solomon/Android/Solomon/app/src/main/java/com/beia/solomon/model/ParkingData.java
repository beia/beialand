package com.beia.solomon.model;

public class ParkingData {
    private long id;
    private Status status;
    private String date;
    private long parkingSpaceId;

    public ParkingData() {
    }

    public ParkingData(long id, Status status, String date, long parkingSpaceId) {
        this.id = id;
        this.status = status;
        this.date = date;
        this.parkingSpaceId = parkingSpaceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getParkingSpace() {
        return parkingSpaceId;
    }

    public void setParkingSpace(long parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }
}
