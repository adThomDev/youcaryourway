package com.yourcaryourway.chat_backend.repositories;

import com.yourcaryourway.chat_backend.models.entity.ChatMessage;
import com.yourcaryourway.chat_backend.models.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findBySenderOrReceiverOrderBySentAt(User sender, User receiver);
}
