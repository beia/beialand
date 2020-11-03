package com.beia_consult_international.solomon.repository;

import com.beia_consult_international.solomon.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}
