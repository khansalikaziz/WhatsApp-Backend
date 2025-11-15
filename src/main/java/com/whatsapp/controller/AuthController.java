package com.whatsapp.controller;

import com.whatsapp.dto.*;
import com.whatsapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

//    @PostMapping("/send-otp")
//    public ResponseEntity<String> sendOtp(@RequestBody OtpRequest request) {
//        authService.sendOtp(request);
//        return ResponseEntity.ok("OTP sent successfully");
//    }
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody OtpRequest request) {
        String otp = authService.sendOtp(request);
        Map<String, String> res = new HashMap<>();
        res.put("message", "OTP sent successfully");
        res.put("otp", otp);
        return ResponseEntity.ok(res);
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody OtpVerifyRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.verifyOtpAndRegister(request);
        return ResponseEntity.ok(response);
    }
}
