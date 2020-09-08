package com.beia_consult_international.solomon.controller;

import com.beia_consult_international.solomon.dto.*;
import com.beia_consult_international.solomon.exception.WrongUserDetailsException;
import com.beia_consult_international.solomon.model.BeaconLocalizationData;
import com.beia_consult_international.solomon.service.BeaconService;
import com.beia_consult_international.solomon.service.CampaignService;
import com.beia_consult_international.solomon.service.MallService;
import com.beia_consult_international.solomon.service.UserService;
import com.beia_consult_international.solomon.service.mapper.BeaconMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mobileApp")
public class MobileAppController {
    private final UserService userService;
    private final MallService mallService;
    private final BeaconService beaconService;
    private final CampaignService campaignService;
    @Value("${solomon.usersPicturesPath}")
    public String usersPath;
    @Value("${solomon.campaignsPicturesPath}")
    public String campaignsPath;

    public MobileAppController(UserService userService, MallService mallService, BeaconService beaconService, CampaignService campaignService) {
        this.userService = userService;
        this.mallService = mallService;
        this.beaconService = beaconService;
        this.campaignService = campaignService;
    }

    @GetMapping("/login")
    public UserDto login(@RequestParam String username, @RequestParam String password) {
        if(!userService.validUserDetails(username, password))
            throw new WrongUserDetailsException();
        return userService.findUserByUsername(username);
    }

    @PostMapping("/signUp")
    public Boolean signUp(@RequestBody UserDto userDto, @RequestParam String password) {
        if(userService.userExists(userDto)) {
            return false;
        }
        userService.save(userDto, password);
        return true;
    }

    @GetMapping("/getMalls")
    public List<MallDto> getMalls() {
        System.out.println("GET MALLS");
        return mallService.findAll();
    }

    @GetMapping("/getBeacons")
    public List<BeaconDto> getBeacons() {
        System.out.println("GET BEACONS");
        return beaconService.findAll()
                .stream()
                .map(BeaconMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/postBeaconTime")
    public void saveBeaconTime(@RequestParam(name = "user_id") long userId,
                               @RequestParam(name = "beacon_id") long beaconId,
                               @RequestBody long seconds) {
        System.out.println("POST BEACON TIME");
        beaconService.saveBeaconTime(userId, beaconId, seconds);
    }

    @PostMapping("/computeLocation/{userId}")
    public LocationDto computeLocation(@RequestBody BeaconLocalizationData beaconLocalizationData,
                                       @PathVariable long userId) {
        System.out.println("COMPUTE LOCATION");
        return beaconService
                .computeAndSaveLocation(beaconLocalizationData, userId);
    }

    @GetMapping("/getCampaigns")
    public List<CampaignDto> getCampaignsFromUser(@RequestParam(name = "companyId") long userId) {
        System.out.println("GET CAMPAIGNS");
        return campaignService.findCampaigns(userId, campaignsPath, usersPath);
    }

    @PostMapping("/saveCampaign")
    public void saveCampaign(@RequestBody CampaignDto campaignDto) throws IOException {
        campaignService.savePicture(Base64.getDecoder().decode(campaignDto.getImage()),
                campaignsPath, campaignDto.getId());
        campaignService.save(campaignDto);
    }
}
