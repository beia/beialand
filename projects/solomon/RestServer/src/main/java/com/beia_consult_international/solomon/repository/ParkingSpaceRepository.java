package com.beia_consult_international.solomon.repository;

import com.beia_consult_international.solomon.model.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
    ParkingSpace findByUID(String UID);
}
