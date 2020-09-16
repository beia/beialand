package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.LocationDto;
import com.beia_consult_international.solomon.model.Location;

import java.time.LocalDateTime;

public abstract class LocationMapper {
    public static LocationDto mapToDto(Location model) {
        return LocationDto
                .builder()
                .id(model.getId())
                .latitude(model.getLatitude())
                .longitude(model.getLongitude())
                .date(model.getDate().toString())
                .build();
    }

    public static Location mapToModel(LocationDto dto) {
        return Location
                .builder()
                .id(dto.getId())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .date(LocalDateTime.parse(dto.getDate()))
                .build();
    }
}
