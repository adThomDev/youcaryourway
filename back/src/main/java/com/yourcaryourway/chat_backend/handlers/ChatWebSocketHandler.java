package com.yourcaryourway.chat_backend.handlers;

import com.yourcaryourway.chat_backend.models.entity.ChatMessage;
import com.yourcaryourway.chat_backend.repositories.ChatMessageRepository;
import com.yourcaryourway.chat_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    //TODO : WiP
    // @Autowired
    // private UserRepository userRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String content = message.getPayload();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);

        //TODO pour le moment les messages sont diffusés à tous les clients connectés, il faudra filtrer par utilisateur
        for (WebSocketSession client : sessions) {
            if (client.isOpen()) {
                client.sendMessage(new TextMessage(content));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}
