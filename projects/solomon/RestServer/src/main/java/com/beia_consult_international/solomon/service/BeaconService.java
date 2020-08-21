package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.BeaconDto;
import com.beia_consult_international.solomon.repository.BeaconRepository;
import com.beia_consult_international.solomon.service.mapper.BeaconMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeaconService {
    private BeaconRepository beaconRepository;

    public BeaconService(BeaconRepository beaconRepository) {
        this.beaconRepository = beaconRepository;
    }

    public List<BeaconDto> findAll() {
        return beaconRepository.findAll()
                .stream()
                .map(BeaconMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
