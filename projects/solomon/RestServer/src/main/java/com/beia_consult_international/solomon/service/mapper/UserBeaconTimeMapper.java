package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.UserBeaconTimeDto;
import com.beia_consult_international.solomon.model.UserBeaconTime;

public abstract class UserBeaconTimeMapper {

    public static UserBeaconTimeDto mapToDto(UserBeaconTime userBeaconTime) {
        return UserBeaconTimeDto
                .builder()
                .id(userBeaconTime.getId())
                .seconds(userBeaconTime.getSeconds())
                .beacon(BeaconMapper.mapToDto(userBeaconTime.getBeacon()))
                .user(UserMapper.mapToDto(userBeaconTime.getUser()))
                .build();
    }

    public static UserBeaconTime mapToModel(UserBeaconTimeDto userBeaconTimeDto) {
        return UserBeaconTime
                .builder()
                .id(userBeaconTimeDto.getId())
                .seconds(userBeaconTimeDto.getSeconds())
                .beacon(BeaconMapper.mapToModel(userBeaconTimeDto.getBeacon()))
                .user(UserMapper.mapToModel(userBeaconTimeDto.getUser()))
                .build();
    }
}
