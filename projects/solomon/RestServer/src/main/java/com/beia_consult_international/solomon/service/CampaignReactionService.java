package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.CampaignReactionDto;
import com.beia_consult_international.solomon.exception.CampaignsNotFoundException;
import com.beia_consult_international.solomon.model.Campaign;
import com.beia_consult_international.solomon.model.CampaignReaction;
import com.beia_consult_international.solomon.repository.CampaignReactionRepository;
import com.beia_consult_international.solomon.repository.CampaignRepository;
import com.beia_consult_international.solomon.service.mapper.CampaignReactionMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignReactionService {
    private final CampaignReactionRepository campaignReactionRepository;
    private final CampaignRepository campaignRepository;

    public CampaignReactionService(CampaignReactionRepository campaignReactionRepository, CampaignRepository campaignRepository) {
        this.campaignReactionRepository = campaignReactionRepository;
        this.campaignRepository = campaignRepository;
    }

    public List<CampaignReactionDto> findAllByCampaignId(long campaignId) {
        return campaignReactionRepository
                .findAllByCampaign(campaignRepository
                        .findById(campaignId)
                        .orElseThrow(CampaignsNotFoundException::new))
                .stream()
                .map(CampaignReactionMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public void save(CampaignReactionDto campaignReactionDto) {
        campaignReactionRepository.save(CampaignReactionMapper
                .mapToModel(campaignReactionDto));
    }
}
