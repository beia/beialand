package com.example.demo.repository;

import com.example.demo.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {
}
