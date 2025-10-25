package com.whatsapp.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupDto {
    private Long id;
    private String name;
    private String description;
    private String groupIcon;
    private Long createdById;
    private String createdByName;
    private List<UserDto> members;
    private List<UserDto> admins;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;
    private LocalDateTime createdAt;
}
