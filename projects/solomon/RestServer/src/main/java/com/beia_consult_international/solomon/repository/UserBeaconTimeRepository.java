package com.beia_consult_international.solomon.repository;

import com.beia_consult_international.solomon.model.Beacon;
import com.beia_consult_international.solomon.model.User;
import com.beia_consult_international.solomon.model.UserBeaconTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBeaconTimeRepository extends JpaRepository<UserBeaconTime, Long> {
    Optional<UserBeaconTime> findUserBeaconTimeByUserAndBeacon(User user, Beacon beacon);
}
