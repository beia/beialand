package com.beia_consult_international.solomon.dto;

import com.beia_consult_international.solomon.model.ParkingStatus;
import lombok.Builder;

@Builder
public class ParkingDataDto {
    private long id;
    private ParkingStatus status;
    private String date;
    private long parkingSpaceId;

    public ParkingDataDto() {
    }

    public ParkingDataDto(long id, ParkingStatus status, String date, long parkingSpaceId) {
        this.id = id;
        this.status = status;
        this.date = date;
        this.parkingSpaceId = parkingSpaceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ParkingStatus getStatus() {
        return status;
    }

    public void setStatus(ParkingStatus status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getParkingSpace() {
        return parkingSpaceId;
    }

    public void setParkingSpace(long parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }
}
