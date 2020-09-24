package com.beia_consult_international.solomon.controller;

import com.beia_consult_international.solomon.dto.CampaignDto;
import com.beia_consult_international.solomon.dto.CampaignReactionDto;
import com.beia_consult_international.solomon.dto.MallDto;
import com.beia_consult_international.solomon.dto.UserDto;
import com.beia_consult_international.solomon.exception.WrongUserDetailsException;
import com.beia_consult_international.solomon.service.CampaignReactionService;
import com.beia_consult_international.solomon.service.CampaignService;
import com.beia_consult_international.solomon.service.MallService;
import com.beia_consult_international.solomon.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/solomon")
public class WebPlatformController {
    private final UserService userService;
    private final CampaignService campaignService;
    private final CampaignReactionService campaignReactionService;
    private final MallService mallService;
    @Value("${solomon.usersPicturesPath}")
    public String usersPath;
    @Value("${solomon.campaignsPicturesPath}")
    public String campaignsPath;

    public WebPlatformController(UserService userService, CampaignService campaignService, CampaignReactionService campaignReactionService, MallService mallService) {
        this.userService = userService;
        this.campaignService = campaignService;
        this.campaignReactionService = campaignReactionService;
        this.mallService = mallService;
    }

    @GetMapping("/login")
    public UserDto login(@RequestParam String username, @RequestParam String password) {
        if(!userService.validUserDetails(username, password))
            throw new WrongUserDetailsException();
        return userService.findUserByUsername(username, usersPath);
    }

    @PostMapping("/register")
    public Boolean signUp(@RequestBody UserDto userDto, @RequestParam String password) {
        if(userService.userExists(userDto)) {
            return false;
        }
        userService.save(userDto, password);
        return true;
    }

    @GetMapping("retailer/campaigns")
    public List<CampaignDto> getCampaigns(@RequestParam long userId) {
        System.out.println("GET CAMPAIGNS");
        return campaignService.findCampaigns(userId, campaignsPath, usersPath);
    }

    @GetMapping("retailer/campaigns/{campaignId}")
    public CampaignDto getCampaign(@PathVariable long campaignId) {
        System.out.println("GET CAMPAIGN");
        return campaignService.findCampaign(campaignId, campaignsPath, usersPath);
    }

    @GetMapping("retailer/oldCampaigns")
    public List<CampaignDto> getOldCampaigns(@RequestParam long userId) {
        System.out.println("GET OLD CAMPAIGNS");
        return campaignService.findOldCampaigns(userId, campaignsPath, usersPath);
    }

    @PostMapping("retailer/addCampaign")
    public CampaignDto saveCampaign(@RequestBody CampaignDto campaignDto) throws IOException {
        CampaignDto savedCampaign = campaignService.save(campaignDto);
        campaignService.savePicture(Base64.getDecoder().decode(campaignDto.getImage()),
                campaignsPath, savedCampaign.getId());
        return savedCampaign;
    }

    @PutMapping("retailer/updateCampaign")
    public CampaignDto updateCampaign(@RequestBody CampaignDto campaignDto) throws IOException {
        campaignService.savePicture(Base64.getDecoder().decode(campaignDto.getImage()),
                campaignsPath, campaignDto.getId());
        return campaignService.save(campaignDto);
    }

    @DeleteMapping("retailer/campaigns/{campaignId}")
    public void deleteCampaign(@PathVariable long campaignId) {
        campaignService.deleteCampaign(campaignId);
    }

    @GetMapping("retailer/campaignsReactions")
    public List<CampaignReactionDto> findAll(@RequestParam long campaignId) {
        return campaignReactionService.findAllByCampaignId(campaignId);
    }

    @GetMapping("mall/getInfo")
    public MallDto getMallInfo(@RequestParam long userId) {
        return mallService.findByUserId(userId);
    }
}
