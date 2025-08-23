package com.yourcaryourway.chat_backend.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcaryourway.chat_backend.models.dto.ChatMessageDTO;
import com.yourcaryourway.chat_backend.models.entity.ChatMessage;
import com.yourcaryourway.chat_backend.models.entity.User;
import com.yourcaryourway.chat_backend.repositories.ChatMessageRepository;
import com.yourcaryourway.chat_backend.repositories.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private final Set<WebSocketSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<WebSocketSession, User> sessionUserMap = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ChatMessageRepository chatMessageRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
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

        // Fetch chat history for the user
        List<ChatMessage> chatHistory = chatMessageRepository.findBySenderOrReceiverOrderBySentAt(user, user);

        // Convert to DTOs
        List<ChatMessageDTO> chatHistoryDTOs = chatHistory.stream()
            .map(message -> new ChatMessageDTO(
                message.getContent(),
                message.getSentAt(),
                message.getSender().getUserId(),
                message.getReceiver().getUserId()
            ))
            .toList();

        // Send chat history to the user
        String chatHistoryJson = objectMapper.writeValueAsString(chatHistoryDTOs);
        session.sendMessage(new TextMessage(chatHistoryJson));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User sender = sessionUserMap.get(session);
        String payload = message.getPayload();

        // Parse the message payload using the injected ObjectMapper
        JsonNode messageJson = objectMapper.readTree(payload);
        int recipientId = messageJson.get("recipientId").asInt();
        String messageContent = messageJson.get("content").asText();

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        // Create the ChatMessage entity
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(messageContent);
        chatMessage.setSentAt(Instant.now());
        chatMessage.setSender(sender);
        chatMessage.setReceiver(recipient);
        chatMessageRepository.save(chatMessage);
        
        // Create the message DTO
        ChatMessageDTO sentMessageDto = new ChatMessageDTO(
            chatMessage.getContent(),
            chatMessage.getSentAt(),
            chatMessage.getSender().getUserId(),
            chatMessage.getReceiver().getUserId()
        );
        String sentMessageJson = objectMapper.writeValueAsString(sentMessageDto);
        
        // Send the message to both the recipient and the sender
        for (Map.Entry<WebSocketSession, User> entry : sessionUserMap.entrySet()) {
            if ((entry.getValue().equals(recipient) || entry.getValue().equals(sender)) && entry.getKey().isOpen()) {
                entry.getKey().sendMessage(new TextMessage(sentMessageJson));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionUserMap.remove(session);
    }
}