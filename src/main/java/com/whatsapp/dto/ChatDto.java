package com.whatsapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatDto {
    private Long id;
    private Long userId;
    private String userName;
    private String userProfilePicture;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;
    private boolean isOnline;
    private LocalDateTime lastSeen;
}
