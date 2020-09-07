package com.beia_consult_international.solomon.service.mapper;

import com.beia_consult_international.solomon.dto.CampaignDto;
import com.beia_consult_international.solomon.model.Campaign;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;

public abstract class CampaignMapper {

    public static CampaignDto mapToDto(Campaign model) {
        return CampaignDto
                .builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .category(model.getCategory())
                .startDate(model.getStartDate().toString())
                .endDate(model.getEndDate().toString())
                .user(UserMapper.mapToDto(model.getUser()))
                .build();
    }

    public static CampaignDto mapToDto(Campaign model, String campaignsPicturesPath, String usersPicturesPath) {
        CampaignDto campaignDto = CampaignDto
                .builder()
                .id(model.getId())
                .title(model.getTitle())
                .description(model.getDescription())
                .category(model.getCategory())
                .startDate(model.getStartDate().toString())
                .endDate(model.getEndDate().toString())
                .user(UserMapper.mapToDto(model.getUser(), usersPicturesPath))
                .build();
        try {
            byte[] image = Files.readAllBytes(
                    Path.of(campaignsPicturesPath
                            + model.getId()
                            + ".jpg"));
            campaignDto.setImage(Base64
                    .getEncoder()
                    .encodeToString(image));
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
                .startDate(LocalDateTime.parse(dto.getStartDate()))
                .endDate(LocalDateTime.parse(dto.getEndDate()))
                .user(UserMapper.mapToModel(dto.getUser()))
                .build();
    }
}
