package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.MallDto;
import com.beia_consult_international.solomon.model.Mall;

import java.util.stream.Collectors;

public abstract class MallMapper {

    public static MallDto mapToDto(Mall mall) {
        return MallDto
                .builder()
                .id(mall.getId())
                .name(mall.getName())
                .latitude(mall.getLatitude())
                .longitude(mall.getLongitude())
                .user(UserMapper.mapToDto(mall.getUser()))
                .parkingSpaces(mall.getParkingSpaces()
                        .stream()
                        .map(ParkingSpaceMapper::mapToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Mall mapToModel(MallDto mallDto) {
        return Mall
                .builder()
                .id(mallDto.getId())
                .name(mallDto.getName())
                .latitude(mallDto.getLatitude())
                .longitude(mallDto.getLongitude())
                .user(UserMapper.mapToModel(mallDto.getUser()))
                .parkingSpaces(mallDto
                        .getParkingSpaces()
                        .stream()
                        .map(ParkingSpaceMapper::mapToModel)
                        .collect(Collectors.toList()))
                .build();
    }
}
