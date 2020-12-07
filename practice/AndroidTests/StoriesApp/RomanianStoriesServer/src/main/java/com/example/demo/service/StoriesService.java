package com.example.demo.service;

import com.example.demo.dto.StoryDto;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.StoryRepository;
import com.example.demo.service.mapper.StoryMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StoriesService {
    private final AuthorRepository authorRepository;
    private final StoryRepository storyRepository;
    private final StoryMapper storyMapper;

    public StoriesService(AuthorRepository authorRepository, StoryRepository storyRepository, StoryMapper storyMapper) {
        this.authorRepository = authorRepository;
        this.storyRepository = storyRepository;
        this.storyMapper = storyMapper;
    }

    public List<StoryDto> getStories(int receivedStories, int batchSize) {
        return storyRepository
                .findAll(PageRequest.of(receivedStories / batchSize, batchSize))
                .get()
                .map(storyMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
