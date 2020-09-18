package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.ParkingSpaceDto;
import com.beia_consult_international.solomon.dto.ParkingStatsDto;
import com.beia_consult_international.solomon.model.ParkingData;
import com.beia_consult_international.solomon.model.ParkingSpace;
import com.beia_consult_international.solomon.model.Status;
import com.beia_consult_international.solomon.repository.ParkingSpaceRepository;
import com.beia_consult_international.solomon.service.mapper.ParkingSpaceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ParkingService {
    private final ParkingSpaceRepository parkingSpaceRepository;

    public ParkingService(ParkingSpaceRepository parkingSpaceRepository) {
        this.parkingSpaceRepository = parkingSpaceRepository;
    }

    public List<ParkingSpaceDto> findAll() {
        return parkingSpaceRepository
                .findAll()
                .stream()
                .map(ParkingSpaceMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public void saveAll(List<ParkingStatsDto> parkingStats) {
        List<ParkingSpace> parkingSpaces = parkingSpaceRepository.findAll();
        parkingSpaces.forEach(parkingSpace -> parkingSpace
                .getParkingData()
                .addAll(parkingStats
                        .stream()
                        .filter(parkingStatsDto -> parkingStatsDto
                                .getLW_EUI()
                                .equals(parkingSpace.getUID()))
                        .map(parkingStatsDto -> ParkingData
                                .builder()
                                .status(parkingStatsDto.getParking_slot_status() == 0
                                        ? Status.FREE
                                        : Status.OCCUPIED)
                                .date(LocalDateTime.parse(parkingStatsDto.getLW_ts()))
                                .parkingSpace(parkingSpace)
                                .build())
                        .collect(Collectors.toList())));
        parkingSpaceRepository.saveAll(parkingSpaces);
    }
}
