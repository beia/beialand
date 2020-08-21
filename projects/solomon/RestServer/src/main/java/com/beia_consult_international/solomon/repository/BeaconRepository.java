package com.beia_consult_international.solomon.repository;

import com.beia_consult_international.solomon.model.Beacon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeaconRepository extends JpaRepository<Beacon, Long> {
}
