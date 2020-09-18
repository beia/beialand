package com.beia_consult_international.solomon.dto;

import com.beia_consult_international.solomon.model.ParkingSpace;
import lombok.Builder;

import java.util.List;

@Builder
public class MallDto {
    private long id;
    private String name;
    private double latitude;
    private double longitude;
    private List<ParkingSpaceDto> parkingSpaces;

    public MallDto() {
    }

    public MallDto(long id, String name, double latitude, double longitude, List<ParkingSpaceDto> parkingSpaces) {
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

    public List<ParkingSpaceDto> getParkingSpaces() {
        return parkingSpaces;
    }

    public void setParkingSpaces(List<ParkingSpaceDto> parkingSpaces) {
        this.parkingSpaces = parkingSpaces;
    }
}
