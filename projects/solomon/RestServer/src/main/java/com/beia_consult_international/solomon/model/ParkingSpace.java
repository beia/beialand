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

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @ManyToOne
    @JoinColumn(name = "mall_id")
    private Mall mall;

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL)
    private List<ParkingData> parkingData;

    public ParkingSpace() {
    }

    public ParkingSpace(long id, double latitude, double longitude, Mall mall, List<ParkingData> parkingData) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mall = mall;
        this.parkingData = parkingData;
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
}
