package com.beia.solomon_smart_shopping.model;

import java.util.List;

public class ParkingSpace {
    private long id;
    private double latitude;
    private double longitude;
    private float rotation;
    private long mallId;
    private List<ParkingData> parkingData;
    private Beacon beacon;

    public ParkingSpace() {
    }

    public ParkingSpace(long id, double latitude, double longitude, float rotation, long mallId, List<ParkingData> parkingData, Beacon beacon) {
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

    public long getMall() {
        return mallId;
    }

    public void setMall(long mallId) {
        this.mallId = mallId;
    }

    public List<ParkingData> getParkingData() {
        return parkingData;
    }

    public void setParkingData(List<ParkingData> parkingData) {
        this.parkingData = parkingData;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }
}
