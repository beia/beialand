package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.exception.CampaignsNotFoundException;
import com.beia_consult_international.solomon.model.Campaign;
import com.beia_consult_international.solomon.model.User;
import com.beia_consult_international.solomon.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public void save(Campaign campaign) {
        campaignRepository.save(campaign);
    }

    public void saveAll(List<Campaign> campaigns) {
        campaignRepository.saveAll(campaigns);
    }

    public List<Campaign> findCampaignsByUser(User user) {
        return campaignRepository
                .findCampaignsByUser(user)
                .orElseThrow(CampaignsNotFoundException::new);
    }

    public void savePicture(byte[] image, String path, long id) throws IOException {
        Files.write(Path.of(path + id + ".jpg"), image);
    }
}
