package com.beia_consult_international.solomon.controller;

import com.beia_consult_international.solomon.dto.CampaignDto;
import com.beia_consult_international.solomon.dto.CampaignReactionDto;
import com.beia_consult_international.solomon.dto.MallDto;
import com.beia_consult_international.solomon.dto.UserDto;
import com.beia_consult_international.solomon.service.CampaignReactionService;
import com.beia_consult_international.solomon.service.CampaignService;
import com.beia_consult_international.solomon.service.MallService;
import com.beia_consult_international.solomon.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
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

//    @RequestMapping(value="/login", method = RequestMethod.POST)
//    @ResponseBody
//    public UserDto login(@RequestBody Map<String, ?> request) {
//        String username = (String)request.get("username");
//        String password = (String)request.get("password");
//        if(!userService.validUserDetails(username, password))
//            throw new WrongUserDetailsException();
//        return userService.findUserByUsername(username, usersPath);
//    }

    @PostMapping("/register")
    @ResponseBody
    public Boolean signUp(@RequestBody UserDto userDto, @RequestParam String password) throws IOException {
        if(userService.userExists(userDto)) {
            return false;
        }
        userService.save(userDto, password);
        return true;
    }

    @GetMapping("retailer/campaigns")
    @ResponseBody
    public List<CampaignDto> getCampaigns(@RequestParam String userName) {
        System.out.println("GET CAMPAIGNS");
        UserDto user = userService.findUserByUsername(userName);
        return campaignService.findCampaigns(user.getId(), campaignsPath, usersPath);
    }

    @GetMapping("retailer/campaigns/{campaignId}")
    @ResponseBody
    public CampaignDto getCampaign(@PathVariable long campaignId) {
        System.out.println("GET CAMPAIGN");
        return campaignService.findCampaign(campaignId, campaignsPath, usersPath);
    }

    @GetMapping("retailer/oldCampaigns")
    @ResponseBody
    public List<CampaignDto> getOldCampaigns() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        UserDto currentUser = userService.findUserByUsername(currentPrincipalName);

        System.out.println("GET OLD CAMPAIGNS");
        return campaignService.findOldCampaigns(currentUser.getId(), campaignsPath, usersPath);
    }

    @PostMapping("retailer/addCampaign")
    @ResponseBody
    public CampaignDto saveCampaign(@RequestBody CampaignDto campaignDto) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        UserDto currentUser = userService.findUserByUsername(currentPrincipalName);
        campaignDto.setUser(currentUser);

        CampaignDto savedCampaign = campaignService.save(campaignDto);
        campaignService.savePicture(Base64.getDecoder().decode(campaignDto.getImage()),
                campaignsPath, savedCampaign.getId());
        savedCampaign.setImage(campaignDto.getImage());
        return savedCampaign;
    }

    @PostMapping("retailer/updateCampaign")
    @ResponseBody
    public CampaignDto updateCampaign(@RequestBody CampaignDto campaignDto) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        UserDto currentUser = userService.findUserByUsername(currentPrincipalName);
        campaignDto.setUser(currentUser);

        campaignService.savePicture(Base64.getDecoder().decode(campaignDto.getImage()),
                campaignsPath, campaignDto.getId());
        CampaignDto savedCampaign = campaignService.save(campaignDto);
        savedCampaign.setImage(campaignDto.getImage());
        return savedCampaign;

    }

    @DeleteMapping("retailer/campaigns/{campaignId}")
    @ResponseBody
    public void deleteCampaign(@PathVariable long campaignId) {
        campaignService.deleteCampaign(campaignId);
    }

    @GetMapping("retailer/campaignsReactions")
    @ResponseBody
    public List<CampaignReactionDto> findAll(@RequestParam long campaignId) {
        return campaignReactionService.findAllByCampaignId(campaignId);
    }

    @GetMapping("mall/getInfo")
    @ResponseBody
    public MallDto getMallInfo(@RequestParam long userId) {
        return mallService.findByUserId(userId);
    }

    @RequestMapping(value="/helloWorld", method = RequestMethod.GET)
    public String getHelloWorldPage(){
        return "helloWorld";
    }

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "login";
    }

    @RequestMapping(value="/dashboard", method = RequestMethod.GET)
    public String getDashboardPage(){
        return "dashboard";
    }

    @RequestMapping(value="/contact", method = RequestMethod.GET)
    public String getContactPage(){
        return "contact";
    }

    @RequestMapping(value="/history", method = RequestMethod.GET)
    public String getHistoryPage(){
        return "history";
    }

    @RequestMapping(value="/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "register";
    }

    @RequestMapping(value="/parkingPlaces", method = RequestMethod.GET)
    public String getParkingPlacesPage(){
        return "parkingPlaces";
    }
}
