package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.model.Beacon;
import com.beia_consult_international.solomon.repository.BeaconRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeaconService {
    private final BeaconRepository beaconRepository;

    public BeaconService(BeaconRepository beaconRepository) {
        this.beaconRepository = beaconRepository;
    }

    public List<Beacon> findAll() {
        return beaconRepository.findAll();
    }
}
