package com.whatsapp.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Component
public class OtpUtil {

    private final Map<String, OtpData> otpStorage = new HashMap<>();
    private final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY = 5 * 60 * 1000; // 5 minutes

    public String generateOtp(String phoneNumber) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }

        String otpValue = otp.toString();
        otpStorage.put(phoneNumber, new OtpData(otpValue, System.currentTimeMillis()));

        // In production, send OTP via SMS service (Twilio, AWS SNS, etc.)
        System.out.println("OTP for " + phoneNumber + ": " + otpValue);

        return otpValue;
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        OtpData otpData = otpStorage.get(phoneNumber);

        if (otpData == null) {
            return false;
        }

        if (System.currentTimeMillis() - otpData.timestamp > OTP_VALIDITY) {
            otpStorage.remove(phoneNumber);
            return false;
        }

        if (otpData.otp.equals(otp)) {
            otpStorage.remove(phoneNumber);
            return true;
        }

        return false;
    }

    private static class OtpData {
        String otp;
        long timestamp;

        OtpData(String otp, long timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}
