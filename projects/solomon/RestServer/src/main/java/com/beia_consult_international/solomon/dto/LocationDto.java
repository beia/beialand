package com.beia_consult_international.solomon.dto;

import lombok.Builder;

@Builder
public class LocationDto {
    private long id;
    private double latitude;
    private double longitude;
    private String date;
    public double localizationError;

    public LocationDto(long id, double latitude, double longitude, String date, double localizationError) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.localizationError = localizationError;
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

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public double getLocalizationError() {
        return localizationError;
    }

    public void setLocalizationError(double localizationError) {
        this.localizationError = localizationError;
    }

    @Override
    public String toString() {
        return "LocationDto{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
