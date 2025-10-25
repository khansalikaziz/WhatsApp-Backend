package com.whatsapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String phoneNumber;
    private String name;
    private String email;
    private String profilePicture;
    private String about;
    private boolean isOnline;
    private LocalDateTime lastSeen;
}
