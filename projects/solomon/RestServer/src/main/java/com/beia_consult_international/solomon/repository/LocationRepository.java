package com.beia_consult_international.solomon.repository;

import com.beia_consult_international.solomon.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
