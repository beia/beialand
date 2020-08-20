package com.beia.solomon.model;

import lombok.Builder;

@Builder
public class Beacon {
    private long id;
    private String name;
    private String major;
    private String minor;
    private double latitude;
    private double longitude;
    private int layer;
    private int floor;
    private String manufacturer;
    private User user;
    private Mall mall;

    public Beacon() {
    }

    public Beacon(long id, String name, String major, String minor, double latitude, double longitude, int layer, int floor, String manufacturer, User user, Mall mall) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.minor = minor;
        this.latitude = latitude;
        this.longitude = longitude;
        this.layer = layer;
        this.floor = floor;
        this.manufacturer = manufacturer;
        this.user = user;
        this.mall = mall;
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

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
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

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Mall getMall() {
        return mall;
    }

    public void setMall(Mall mall) {
        this.mall = mall;
    }
}
