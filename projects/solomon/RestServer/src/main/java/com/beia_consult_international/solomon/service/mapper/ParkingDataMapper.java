package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.ParkingDataDto;
import com.beia_consult_international.solomon.model.ParkingData;

import java.time.LocalDateTime;

public abstract class ParkingDataMapper {

    public static ParkingDataDto mapToDto(ParkingData model) {
        return ParkingDataDto
                .builder()
                .id(model.getId())
                .parkingSpaceId(model.getParkingSpace().getId())
                .status(model.getStatus())
                .date(model.getDate().toString())
                .build();
    }

    public static ParkingData mapToModel(ParkingDataDto dto) {
        return ParkingData
                .builder()
                .id(dto.getId())
                .status(dto.getStatus())
                .date(LocalDateTime.parse(dto.getDate()))
                .build();
    }
}
