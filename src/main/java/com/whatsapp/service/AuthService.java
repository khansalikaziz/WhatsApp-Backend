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

    public String sendOtp(OtpRequest request) {
        String otp = otpUtil.generateOtp(request.getPhoneNumber());
        // In production, integrate with SMS service (Twilio, AWS SNS, etc.)
        System.out.println("OTP sent to " + request.getPhoneNumber() + ": " + otp);
        return otp;
    }

    @Transactional
    public AuthResponse verifyOtpAndRegister(RegisterRequest request) {
        System.out.println(request.getName() + "," + request.getEmail() + "," + request.getPhoneNumber());

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .map(existingUser -> {
                    // ✅ Update existing user's details if they’ve changed
                    existingUser.setName(request.getName());
                    existingUser.setEmail(request.getEmail());
                    existingUser.setProfilePicture(request.getProfilePicture());
                    existingUser.setAbout(
                            existingUser.getAbout() != null ? existingUser.getAbout() : "Hey there! I am using RailChat."
                    );
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // ✅ Create a new user if not exists
                    System.out.println("Creating new user...");
                    User newUser = new User();
                    newUser.setPhoneNumber(request.getPhoneNumber());
                    newUser.setName(request.getName());
                    newUser.setEmail(request.getEmail());
                    newUser.setProfilePicture(request.getProfilePicture());
                    newUser.setAbout("Hey there! I am using RailChat.");
                    return userRepository.save(newUser);
                });

        // ✅ Generate JWT Token
        String token = jwtUtil.generateToken(user.getPhoneNumber());
        UserDto userDto = mapToUserDto(user);

        return new AuthResponse(token, userDto);
    }



    public AuthResponse login(OtpVerifyRequest request) {
        if (!otpUtil.verifyOtp(request.getPhoneNumber(), request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setPhoneNumber(request.getPhoneNumber());
                    // Set other default fields if needed
                    newUser.setOnline(false);
                    newUser.setName(""); // Optional placeholder
                    return userRepository.save(newUser);
                });

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
