package com.beia.solomon.data;

public class Location {
    private double latitude;
    private double longitude;
    public double localizationError;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(double latitude, double longitude, double localizationError) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.localizationError = localizationError;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
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
