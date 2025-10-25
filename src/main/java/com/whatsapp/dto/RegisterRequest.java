package com.whatsapp.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String phoneNumber;
    private String name;
    private String email;
    private String profilePicture; // Base64 or URL
}
