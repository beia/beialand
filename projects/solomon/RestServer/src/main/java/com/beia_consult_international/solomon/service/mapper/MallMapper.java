package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.MallDto;
import com.beia_consult_international.solomon.model.Mall;

public abstract class MallMapper {

    public static MallDto mapToDto(Mall mall) {
        return MallDto
                .builder()
                .id(mall.getId())
                .name(mall.getName())
                .latitude(mall.getLatitude())
                .longitude(mall.getLongitude())
                .build();
    }

    public static Mall mapToModel(MallDto mallDto) {
        return Mall
                .builder()
                .id(mallDto.getId())
                .name(mallDto.getName())
                .latitude(mallDto.getLatitude())
                .longitude(mallDto.getLongitude())
                .build();
    }
}
