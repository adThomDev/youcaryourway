package com.yourcaryourway.chat_backend.repositories;

import com.yourcaryourway.chat_backend.models.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
}
