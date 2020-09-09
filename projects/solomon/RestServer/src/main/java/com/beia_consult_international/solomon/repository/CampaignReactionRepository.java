package com.beia_consult_international.solomon.repository;

import com.beia_consult_international.solomon.model.Campaign;
import com.beia_consult_international.solomon.model.CampaignReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignReactionRepository extends JpaRepository<CampaignReaction, Long> {
    List<CampaignReaction> findAllByCampaign(Campaign campaign);
}
