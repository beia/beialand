package com.beia_consult_international.solomon.model;

import lombok.Builder;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
public class ParkingSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uid", unique = true)
    private String UID;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private float rotation;

    @ManyToOne
    @JoinColumn(name = "mall_id")
    private Mall mall;

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL)
    private List<ParkingData> parkingData;

    @OneToOne
    @JoinColumn(name = "beacon_id")
    private Beacon beacon;

    public ParkingSpace() {
    }

    public ParkingSpace(long id, String UID, double latitude, double longitude, float rotation, Mall mall, List<ParkingData> parkingData, Beacon beacon) {
        this.id = id;
        this.UID = UID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rotation = rotation;
        this.mall = mall;
        this.parkingData = parkingData;
        this.beacon = beacon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
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

    public Mall getMall() {
        return mall;
    }

    public void setMall(Mall mall) {
        this.mall = mall;
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
