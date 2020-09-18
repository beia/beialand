package com.beia_consult_international.solomon.controller;

import com.beia_consult_international.solomon.dto.ParkingStatsDto;
import com.beia_consult_international.solomon.service.ParkingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parking")
public class ParkingModuleController {
    private final ParkingService parkingService;

    public ParkingModuleController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping
    public void postParkingStats(@RequestBody List<ParkingStatsDto> parkingStats) {
        parkingService.saveAll(parkingStats);
    }
}
