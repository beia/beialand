package com.example.demo.service.mapper;

import com.example.demo.dto.AuthorDto;
import com.example.demo.model.Author;
import org.mapstruct.Mapper;
import org.springframework.transaction.annotation.Transactional;

@Mapper(componentModel = "spring")
@Transactional
public interface AuthorMapper {
    AuthorDto mapToDto(Author article);
    Author mapToModel(AuthorDto authorDto);
}
