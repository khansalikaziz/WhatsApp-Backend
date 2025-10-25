package com.whatsapp.controller;

import com.whatsapp.dto.UserDto;
import com.whatsapp.service.FileStorageService;
import com.whatsapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        String phoneNumber = authentication.getName();
        UserDto user = userService.getUserByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateProfile(
            @PathVariable Long userId,
            @RequestBody UserDto userDto) {
        UserDto updated = userService.updateProfile(userId, userDto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{userId}/profile-picture")
    public ResponseEntity<String> uploadProfilePicture(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.storeFile(file, "profiles");
        UserDto userDto = new UserDto();
        userDto.setProfilePicture(fileUrl);
        userService.updateProfile(userId, userDto);
        return ResponseEntity.ok(fileUrl);
    }

    @PostMapping("/{userId}/online-status")
    public ResponseEntity<Void> updateOnlineStatus(
            @PathVariable Long userId,
            @RequestParam boolean isOnline) {
        userService.updateOnlineStatus(userId, isOnline);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/contacts")
    public ResponseEntity<List<UserDto>> getContacts(@PathVariable Long userId) {
        List<UserDto> contacts = userService.getContacts(userId);
        return ResponseEntity.ok(contacts);
    }

    @PostMapping("/{userId}/contacts")
    public ResponseEntity<Void> addContact(
            @PathVariable Long userId,
            @RequestParam String phoneNumber) {
        userService.addContact(userId, phoneNumber);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/sync-contacts")
    public ResponseEntity<List<UserDto>> syncContacts(
            @PathVariable Long userId,
            @RequestBody List<String> phoneNumbers) {
        List<UserDto> registeredContacts = userService.syncContacts(userId, phoneNumbers);
        return ResponseEntity.ok(registeredContacts);
    }
}
