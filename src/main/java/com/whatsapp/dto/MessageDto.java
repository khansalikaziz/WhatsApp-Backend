package com.whatsapp.dto;

import com.whatsapp.model.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderProfilePicture;
    private Long chatId;
    private Long groupId;
    private String content;
    private Message.MessageType messageType;
    private String mediaUrl;
    private String thumbnailUrl;
    private Long mediaSize;
    private String fileName;
    private LocalDateTime timestamp;
    private Message.MessageStatus status;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
}
