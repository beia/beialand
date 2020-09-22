package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.BeaconDto;
import com.beia_consult_international.solomon.model.Beacon;

public abstract class BeaconMapper {

    public static BeaconDto mapToDto(Beacon beacon) {
        return BeaconDto
                .builder()
                .id(beacon.getId())
                .manufacturerId(beacon.getManufacturerId())
                .name(beacon.getName())
                .latitude(beacon.getLatitude())
                .longitude(beacon.getLongitude())
                .floor(beacon.getFloor())
                .layer(beacon.getLayer())
                .major(beacon.getMajor())
                .minor(beacon.getMinor())
                .manufacturer(beacon.getManufacturer())
                .type(beacon.getType())
                .mall(MallMapper.mapToDto(beacon.getMall()))
                .user(UserMapper.mapToDto(beacon.getUser()))
                .build();
    }

    public static Beacon mapToModel(BeaconDto beaconDto) {
        return Beacon
                .builder()
                .id(beaconDto.getId())
                .manufacturerId(beaconDto.getManufacturerId())
                .name(beaconDto.getName())
                .latitude(beaconDto.getLatitude())
                .longitude(beaconDto.getLongitude())
                .floor(beaconDto.getFloor())
                .layer(beaconDto.getLayer())
                .major(beaconDto.getMajor())
                .minor(beaconDto.getMinor())
                .manufacturer(beaconDto.getManufacturer())
                .type(beaconDto.getType())
                .mall(MallMapper.mapToModel(beaconDto.getMall()))
                .user(UserMapper.mapToModel(beaconDto.getUser()))
                .build();
    }
}

