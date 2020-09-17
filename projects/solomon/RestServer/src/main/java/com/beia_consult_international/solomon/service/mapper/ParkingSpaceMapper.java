package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.ParkingSpaceDto;
import com.beia_consult_international.solomon.model.ParkingSpace;

import java.util.stream.Collectors;

public abstract class ParkingSpaceMapper {

    public static ParkingSpaceDto mapToDto(ParkingSpace model) {
        return ParkingSpaceDto
                .builder()
                .id(model.getId())
                .latitude(model.getLatitude())
                .longitude(model.getLongitude())
                .mallId(model.getMall().getId())
                .parkingData(model
                        .getParkingData()
                        .stream()
                        .map(ParkingDataMapper::mapToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static ParkingSpace mapToModel(ParkingSpaceDto dto) {
        return ParkingSpace
                .builder()
                .id(dto.getId())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .parkingData(dto
                        .getParkingData()
                        .stream()
                        .map(ParkingDataMapper::mapToModel)
                        .collect(Collectors.toList()))
                .build();
    }
}
