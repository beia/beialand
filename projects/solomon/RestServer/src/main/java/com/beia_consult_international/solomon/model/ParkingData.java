package com.beia_consult_international.solomon.model;

import lombok.Builder;

import javax.persistence.*;

@Entity
@Builder
public class ParkingData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "parking_space_id")
    private ParkingSpace parkingSpace;

    public ParkingData() {
    }

    public ParkingData(long id, Status status, ParkingSpace parkingSpace) {
        this.id = id;
        this.status = status;
        this.parkingSpace = parkingSpace;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ParkingSpace getParkingSpace() {
        return parkingSpace;
    }

    public void setParkingSpace(ParkingSpace parkingSpace) {
        this.parkingSpace = parkingSpace;
    }
}
