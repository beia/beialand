package com.beia_consult_international.solomon.model;

import com.sun.istack.NotNull;
import lombok.Builder;

import javax.persistence.*;

@Entity
@Builder
public class Beacon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "manufacturer_id", nullable = false)
    private String manufacturerId;

    @Column(nullable = false)
    private String name;

    private int major;

    private int minor;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private int layer;

    @Column(nullable = false)
    private int floor;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private BeaconType type;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_mall", nullable = false)
    private Mall mall;

    public Beacon() {
    }

    public Beacon(long id, String manufacturerId, String name, int major, int minor, double latitude, double longitude, int layer, int floor, String manufacturer, BeaconType type, User user, Mall mall) {
        this.id = id;
        this.manufacturerId = manufacturerId;
        this.name = name;
        this.major = major;
        this.minor = minor;
        this.latitude = latitude;
        this.longitude = longitude;
        this.layer = layer;
        this.floor = floor;
        this.manufacturer = manufacturer;
        this.type = type;
        this.user = user;
        this.mall = mall;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
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

    public BeaconType getType() {
        return type;
    }

    public void setType(BeaconType type) {
        this.type = type;
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

    @Override
    public String toString() {
        return "Beacon{" +
                "id=" + id +
                ", manufacturerId='" + manufacturerId + '\'' +
                ", name='" + name + '\'' +
                ", major='" + major + '\'' +
                ", minor='" + minor + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", layer=" + layer +
                ", floor=" + floor +
                ", manufacturer='" + manufacturer + '\'' +
                ", user=" + user +
                ", mall=" + mall +
                '}';
    }
}
