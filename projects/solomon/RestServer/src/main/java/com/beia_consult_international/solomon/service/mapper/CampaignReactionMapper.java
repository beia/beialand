package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.CampaignReactionDto;
import com.beia_consult_international.solomon.model.CampaignReaction;

import java.time.LocalDateTime;

public abstract class CampaignReactionMapper {
    public static CampaignReactionDto mapToDto(CampaignReaction model) {
        return CampaignReactionDto
                .builder()
                .id(model.getId())
                .user(UserMapper.mapToDto(model.getUser()))
                .campaign(CampaignMapper.mapToDto(model.getCampaign()))
                .date(model.getDate().toString())
                .build();
    }

    public static CampaignReaction mapToModel(CampaignReactionDto dto) {
        return CampaignReaction
                .builder()
                .id(dto.getId())
                .user(UserMapper.mapToModel(dto.getUser()))
                .campaign(CampaignMapper.mapToModel(dto.getCampaign()))
                .date(LocalDateTime.parse(dto.getDate()))
                .build();
    }
}
