package com.beia_consult_international.solomon.repository;

import com.beia_consult_international.solomon.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
