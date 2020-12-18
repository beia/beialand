package com.beia.solomon_smart_shopping.model;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;

@Builder
public class Mall implements Serializable {
    private long id;
    private String name;
    private double latitude;
    private double longitude;
    private List<ParkingSpace> parkingSpaces;

    public Mall() {
    }

    public Mall(long id, String name, double latitude, double longitude, List<ParkingSpace> parkingSpaces) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkingSpaces = parkingSpaces;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<ParkingSpace> getParkingSpaces() {
        return parkingSpaces;
    }

    public void setParkingSpaces(List<ParkingSpace> parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }
}
