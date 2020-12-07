package com.example.demo.controller;

import com.example.demo.dto.StoryDto;
import com.example.demo.service.StoriesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mobileApp")
public class MobileAppController {

    private final StoriesService storyService;
    @Value("${romanianStories.batchSize}")
    private int batchSize;

    public MobileAppController(StoriesService storyService) {
        this.storyService = storyService;
    }

    @GetMapping("/getStories")
    public List<StoryDto> getStories(@RequestParam int receivedStories) {
        return storyService
                .getStories(receivedStories, batchSize);
    }
}
