package com.beia_consult_international.solomon.controller;

import com.beia_consult_international.solomon.dto.*;
import com.beia_consult_international.solomon.exception.WrongUserDetailsException;
import com.beia_consult_international.solomon.model.BeaconLocalizationData;
import com.beia_consult_international.solomon.model.Message;
import com.beia_consult_international.solomon.service.*;
import com.beia_consult_international.solomon.service.mapper.BeaconMapper;
import com.google.firebase.messaging.FirebaseMessagingException;
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
    private final CampaignReactionService campaignReactionService;
    private final ConversationService conversationService;
    @Value("${solomon.usersPicturesPath}")
    public String usersPath;
    @Value("${solomon.campaignsPicturesPath}")
    public String campaignsPath;

    public MobileAppController(UserService userService, MallService mallService, BeaconService beaconService, CampaignService campaignService, CampaignReactionService campaignReactionService, ConversationService conversationService) {
        this.userService = userService;
        this.mallService = mallService;
        this.beaconService = beaconService;
        this.campaignService = campaignService;
        this.campaignReactionService = campaignReactionService;
        this.conversationService = conversationService;
    }

    @GetMapping("/login")
    public UserDto login(@RequestParam String username, @RequestParam String password) {
        if(!userService.validUserDetails(username, password))
            throw new WrongUserDetailsException();
        return userService.findUserByUsername(username);
    }

    @PostMapping("/fcmToken")
    public void saveFCMToken(@RequestParam long userId, @RequestBody String token) {
        userService.saveToken(userId, token);
    }

    @PostMapping("/signUp")
    public Boolean signUp(@RequestBody UserDto userDto, @RequestParam String password) throws IOException {
        if(userService.userExists(userDto)) {
            return false;
        }
        userService.save(userDto, password);
        return true;
    }

    @PostMapping("/updateUser")
    public void updateUser(@RequestBody UserDto userDto, @RequestParam String password) throws IOException {
        userService.save(userDto, password);
    }

    @PostMapping("/updateProfilePicture/{userId}")
    public void updateProfilePicture(@RequestBody String profilePicture, @PathVariable long userId) throws IOException {
        byte[] decodedImage = Base64
                .getDecoder()
                .decode(profilePicture.substring(1, profilePicture.length() - 1));
        userService.savePicture(
                decodedImage,
                usersPath,
                userId);
    }

    @GetMapping("/getProfilePicture/{userId}")
    public String getProfilePicture(@PathVariable long userId) {
        return userService.findById(userId).getImage();
    }

    @GetMapping("/getMalls")
    public List<MallDto> getMalls() {
        System.out.println("GET MALLS");
        return mallService.findAllForMobileUser();
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

    @PostMapping("/saveCampaignReaction")
    public void saveCampaignReaction(@RequestBody CampaignReactionDto campaignReactionDto) {
        campaignReactionService.save(campaignReactionDto);
    }

    @GetMapping("/getHeatmapLocations")
    public List<LocationDto> getHeatmapLocations(@RequestParam String startDate, @RequestParam String endDate) {
        return beaconService.findHeatmapLocations(startDate, endDate);
    }

    @PostMapping("/findChatAgent")
    public void findChatAgent(@RequestParam long userId) throws FirebaseMessagingException {
        conversationService.sendChatNotificationsToAllAgents(userId);
    }

    @PostMapping("/startConversation")
    public ConversationDto startConversation(@RequestParam long agentId, @RequestParam long userId) throws FirebaseMessagingException {
        return conversationService
                .startConversation(agentId, userId);
    }

    @GetMapping("/getConversation")
    public ConversationDto getConversation(@RequestParam long conversationId) {
        return conversationService.findById(conversationId);
    }

    @GetMapping("/getConversationByUserId")
    public ConversationDto getConversationByUserId(@RequestParam long userId) {
        return conversationService.findByUserId(userId);
    }

    @GetMapping("/getConversationByAgentId")
    public ConversationDto getConversationByAgentId(@RequestParam long agentId) {
        return conversationService.findByAgentId(agentId);
    }

    @GetMapping("/getConversationMessages")
    public List<MessageDto> getConversationMessages(@RequestParam long conversationId) {
        return conversationService.findMessagesByConversationId(conversationId);
    }

    @PostMapping("/sendMessage")
    public MessageDto sendMessage(@RequestBody MessageDto message) {
        return conversationService.saveMessage(message);
    }

    @PostMapping("/finishConversation")
    public void finishConversation(@RequestParam long conversationId) {
        conversationService.finishConversation(conversationId);
    }
}
