package com.beia_consult_international.solomon.repository;

import com.beia_consult_international.solomon.model.Conversation;
import com.beia_consult_international.solomon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<List<Conversation>> findByUser2(User user);
}
