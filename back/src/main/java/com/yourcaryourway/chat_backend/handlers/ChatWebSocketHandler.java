package com.yourcaryourway.chat_backend.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcaryourway.chat_backend.models.entity.ChatMessage;
import com.yourcaryourway.chat_backend.models.entity.User;
import com.yourcaryourway.chat_backend.repositories.ChatMessageRepository;
import com.yourcaryourway.chat_backend.repositories.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<WebSocketSession, User> sessionUserMap = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extract user information from the session (e.g., query parameters or headers)
        String userId = session.getUri().getQuery().split("=")[1]; // Example: ?userId=1
        User user = userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Associate the session with the user
        sessionUserMap.put(session, user);
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User sender = sessionUserMap.get(session);
        String content = message.getPayload();

        // Parse recipient ID from the message payload (example JSON: {"recipientId": 2, "content": "Hello"})
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode messageJson = objectMapper.readTree(content);
        int recipientId = messageJson.get("recipientId").asInt();
        String messageContent = messageJson.get("content").asText();

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(messageContent);
        chatMessage.setSentAt(LocalDateTime.now());
        chatMessage.setSender(sender);
        chatMessage.setReceiver(recipient);
        chatMessageRepository.save(chatMessage);

        // Send the message to the recipient's session
        for (Map.Entry<WebSocketSession, User> entry : sessionUserMap.entrySet()) {
            if (entry.getValue().equals(recipient) && entry.getKey().isOpen()) {
                entry.getKey().sendMessage(new TextMessage(messageContent));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionUserMap.remove(session);
    }
}