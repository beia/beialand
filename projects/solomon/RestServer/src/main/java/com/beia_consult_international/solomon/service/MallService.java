package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.dto.MallDto;
import com.beia_consult_international.solomon.repository.MallRepository;
import com.beia_consult_international.solomon.service.mapper.MallMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallService {
    private final MallRepository mallRepository;

    public MallService(MallRepository mallRepository) {
        this.mallRepository = mallRepository;
    }

    public List<MallDto> findAll() {
        return mallRepository
                .findAll()
                .stream()
                .map(MallMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
