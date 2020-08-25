package com.beia_consult_international.solomon.service;

import com.beia_consult_international.solomon.model.Mall;
import com.beia_consult_international.solomon.repository.MallRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MallService {
    private final MallRepository mallRepository;

    public MallService(MallRepository mallRepository) {
        this.mallRepository = mallRepository;
    }

    public List<Mall> findAll() {
        return mallRepository.findAll();
    }
}
