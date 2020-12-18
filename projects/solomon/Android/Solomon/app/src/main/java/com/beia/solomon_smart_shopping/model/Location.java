package com.beia.solomon_smart_shopping.model;

import lombok.Builder;

@Builder
public class Location {
    private long id;
    private double latitude;
    private double longitude;
    public double localizationError;


    public Location(long id, double latitude, double longitude, double localizationError) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public double getLocalizationError() {
        return localizationError;
    }

    public void setLocalizationError(double localizationError) {
        this.localizationError = localizationError;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", localizationError=" + localizationError +
                '}';
    }
}
