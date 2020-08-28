package com.beia_consult_international.solomon.repository;

import com.beia_consult_international.solomon.model.Campaign;
import com.beia_consult_international.solomon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    Optional<List<Campaign>> findCampaignsByUser(User user);
}
