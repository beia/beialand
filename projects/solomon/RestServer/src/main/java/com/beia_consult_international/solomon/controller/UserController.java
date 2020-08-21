package com.beia_consult_international.solomon.controller;

import com.beia_consult_international.solomon.dto.BeaconDto;
import com.beia_consult_international.solomon.dto.MallDto;
import com.beia_consult_international.solomon.dto.UserDto;
import com.beia_consult_international.solomon.exception.WrongUserDetailsException;
import com.beia_consult_international.solomon.service.BeaconService;
import com.beia_consult_international.solomon.service.MallService;
import com.beia_consult_international.solomon.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final MallService mallService;
    private final BeaconService beaconService;

    public UserController(UserService userService, MallService mallService, BeaconService beaconService) {
        this.userService = userService;
        this.mallService = mallService;
        this.beaconService = beaconService;
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
        return beaconService.findAll();
    }
}
