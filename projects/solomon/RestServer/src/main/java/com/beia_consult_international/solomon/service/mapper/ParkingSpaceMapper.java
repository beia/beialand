package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.ParkingDataDto;
import com.beia_consult_international.solomon.dto.ParkingSpaceDto;
import com.beia_consult_international.solomon.model.ParkingData;
import com.beia_consult_international.solomon.model.ParkingSpace;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ParkingSpaceMapper {

    public static ParkingSpaceDto mapToDto(ParkingSpace model) {
        return ParkingSpaceDto
                .builder()
                .id(model.getId())
                .latitude(model.getLatitude())
                .longitude(model.getLongitude())
                .rotation(model.getRotation())
                .mallId(model.getMall().getId())
                .parkingData(model
                        .getParkingData()
                        .stream()
                        .map(ParkingDataMapper::mapToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static ParkingSpaceDto mapToDtoMobileUser(ParkingSpace model) {
        ParkingData parkingData = model.getParkingData().get(model.getParkingData().size() -1);
        return ParkingSpaceDto
                .builder()
                .id(model.getId())
                .latitude(model.getLatitude())
                .longitude(model.getLongitude())
                .rotation(model.getRotation())
                .mallId(model.getMall().getId())
                .parkingData(List.of(ParkingDataDto
                        .builder()
                        .id(parkingData.getId())
                        .status(parkingData.getStatus())
                        .date(parkingData.getDate().toString())
                        .parkingSpaceId(parkingData.getParkingSpace().getId())
                        .build()))
                .build();
    }

    public static ParkingSpace mapToModel(ParkingSpaceDto dto) {
        return ParkingSpace
                .builder()
                .id(dto.getId())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .rotation(dto.getRotation())
                .parkingData(dto
                        .getParkingData()
                        .stream()
                        .map(ParkingDataMapper::mapToModel)
                        .collect(Collectors.toList()))
                .build();
    }
}
