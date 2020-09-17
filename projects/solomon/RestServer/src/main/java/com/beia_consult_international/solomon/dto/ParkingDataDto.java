package com.beia_consult_international.solomon.dto;

import com.beia_consult_international.solomon.model.Status;
import lombok.Builder;

@Builder
public class ParkingDataDto {
    private long id;
    private Status status;
    private long parkingSpaceId;

    public ParkingDataDto() {
    }

    public ParkingDataDto(long id, Status status, long parkingSpaceId) {
        this.id = id;
        this.status = status;
        this.parkingSpaceId = parkingSpaceId;
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

    public long getParkingSpace() {
        return parkingSpaceId;
    }

    public void setParkingSpace(long parkingSpaceId) {
        this.parkingSpaceId = parkingSpaceId;
    }
}
