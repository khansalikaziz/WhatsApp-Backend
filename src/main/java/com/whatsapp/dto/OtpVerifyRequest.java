package com.whatsapp.dto;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String phoneNumber;
    private String otp;
}
