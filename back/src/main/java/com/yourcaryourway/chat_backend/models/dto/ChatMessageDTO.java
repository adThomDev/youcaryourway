package com.yourcaryourway.chat_backend.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageDTO {
    private String content;
    private Instant sentAt;
    private Integer senderId;
    private Integer receiverId;
}
