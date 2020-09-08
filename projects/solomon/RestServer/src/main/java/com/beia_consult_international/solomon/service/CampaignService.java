package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.CampaignDto;
import com.beia_consult_international.solomon.exception.CampaignsNotFoundException;
import com.beia_consult_international.solomon.exception.UserNotFoundException;
import com.beia_consult_international.solomon.model.Campaign;
import com.beia_consult_international.solomon.model.User;
import com.beia_consult_international.solomon.repository.CampaignRepository;
import com.beia_consult_international.solomon.repository.UserRepository;
import com.beia_consult_international.solomon.service.mapper.CampaignMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;

    public CampaignService(CampaignRepository campaignRepository, UserRepository userRepository) {
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
    }

    public CampaignDto save(CampaignDto campaignDto) {
        return CampaignMapper.mapToDto(campaignRepository
                .save(CampaignMapper.mapToModel(campaignDto)));
    }

    public void saveAll(List<Campaign> campaigns) {
        campaignRepository.saveAll(campaigns);
    }

    public List<CampaignDto> findCampaigns(long userId, String campaignsPicturesPath, String usersPicturesPath) {
        return findCampaignsByUserId(userId)
                .stream()
                .filter(campaign -> campaign.getEndDate().compareTo(LocalDateTime.now()) >= 0
                        && campaign.getStartDate().compareTo(LocalDateTime.now()) <= 0)
                .map(campaign -> CampaignMapper.mapToDto(campaign, campaignsPicturesPath, usersPicturesPath))
                .collect(Collectors.toList());
    }

    public CampaignDto findCampaign(long campaignId, String campaignsPicturesPath, String usersPicturesPath) {
        return campaignRepository.findById(campaignId)
                .map(campaign -> CampaignMapper.mapToDto(campaign, campaignsPicturesPath, usersPicturesPath))
                .orElseThrow(CampaignsNotFoundException::new);
    }

    public List<CampaignDto> findOldCampaigns(long userId, String campaignsPicturesPath, String usersPicturesPath) {
        return findCampaignsByUserId(userId)
                .stream()
                .filter(campaign -> campaign.getEndDate().compareTo(LocalDateTime.now()) < 0
                        || campaign.getStartDate().compareTo(LocalDateTime.now()) > 0)
                .map(campaign -> CampaignMapper.mapToDto(campaign, campaignsPicturesPath, usersPicturesPath))
                .collect(Collectors.toList());
    }

    public void savePicture(byte[] image, String path, long id) throws IOException {
        Files.write(Path.of(path + id + ".jpg"), image);
    }

    public void deleteCampaign(long campaignId) {
        campaignRepository.deleteById(campaignId);
    }

    private List<Campaign> findCampaignsByUserId(long userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(UserNotFoundException::new);
        return campaignRepository
                .findCampaignsByUser(user)
                .orElseThrow(CampaignsNotFoundException::new);
    }
}
