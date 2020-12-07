package com.example.demo.service.mapper;

import com.example.demo.dto.StoryDto;
import com.example.demo.model.Story;
import org.mapstruct.Mapper;
import org.springframework.transaction.annotation.Transactional;

@Mapper(componentModel = "spring")
@Transactional
public interface StoryMapper {
    StoryDto mapToDto(Story story);
    Story mapToModel(StoryDto storyDto);
}
