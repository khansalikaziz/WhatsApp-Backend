package com.whatsapp.service;

import com.whatsapp.dto.*;
import com.whatsapp.model.User;
import com.whatsapp.repository.UserRepository;
import com.whatsapp.util.JwtUtil;
import com.whatsapp.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpUtil otpUtil;
    private final JwtUtil jwtUtil;

    public void sendOtp(OtpRequest request) {
        String otp = otpUtil.generateOtp(request.getPhoneNumber());
        // In production, integrate with SMS service (Twilio, AWS SNS, etc.)
        System.out.println("OTP sent to " + request.getPhoneNumber() + ": " + otp);
    }

    @Transactional
    public AuthResponse verifyOtpAndRegister(RegisterRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setPhoneNumber(request.getPhoneNumber());
                    newUser.setName(request.getName());
                    newUser.setEmail(request.getEmail());
                    newUser.setProfilePicture(request.getProfilePicture());
                    newUser.setAbout("Hey there! I am using WhatsApp.");
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user.getPhoneNumber());
        UserDto userDto = mapToUserDto(user);

        return new AuthResponse(token, userDto);
    }

    public AuthResponse login(OtpVerifyRequest request) {
        if (!otpUtil.verifyOtp(request.getPhoneNumber(), request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getPhoneNumber());
        UserDto userDto = mapToUserDto(user);

        return new AuthResponse(token, userDto);
    }

    private UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setAbout(user.getAbout());
        dto.setOnline(user.isOnline());
        dto.setLastSeen(user.getLastSeen());
        return dto;
    }
}
