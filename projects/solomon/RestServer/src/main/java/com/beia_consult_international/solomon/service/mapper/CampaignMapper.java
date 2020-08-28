package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.CampaignDto;
import com.beia_consult_international.solomon.model.Campaign;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CampaignMapper {

    public static CampaignDto mapToDto(Campaign model) {
        return CampaignDto
                .builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .category(model.getCategory())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .user(UserMapper.mapToDto(model.getUser()))
                .build();
    }

    public static CampaignDto mapToDto(Campaign model, String campaignsPicturesPath) {
        CampaignDto campaignDto = CampaignDto
                .builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .category(model.getCategory())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .user(UserMapper.mapToDto(model.getUser()))
                .build();
        try {
            campaignDto.setImage(Files.readAllBytes(
                    Path.of(campaignsPicturesPath
                            + model.getId()
                            + ".jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return campaignDto;
    }

    public static Campaign mapToModel(CampaignDto dto) {
        return Campaign
                .builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .user(UserMapper.mapToModel(dto.getUser()))
                .build();
    }
}
