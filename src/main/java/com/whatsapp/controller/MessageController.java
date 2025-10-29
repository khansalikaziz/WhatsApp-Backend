package com.whatsapp.controller;

import com.whatsapp.dto.MessageDto;
import com.whatsapp.model.Message;
import com.whatsapp.service.FileStorageService;
import com.whatsapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final FileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageDto messageDto) {
        MessageDto savedMessage = messageService.sendMessage(messageDto);

        if (messageDto.getChatId() != null) {
            // Send to specific user
            messagingTemplate.convertAndSend(
                    "/topic/chat." + messageDto.getChatId(), savedMessage);
        } else if (messageDto.getGroupId() != null) {
            // Send to group
            messagingTemplate.convertAndSend(
                    "/topic/group." + messageDto.getGroupId(), savedMessage);
        }
    }

    @MessageMapping("/chat.updateStatus")
    public void updateMessageStatus(@Payload MessageDto messageDto) {
        messageService.updateMessageStatus(messageDto.getId(), messageDto.getStatus());

        // Notify sender about status update
        messagingTemplate.convertAndSend(
                "/topic/status." + messageDto.getSenderId(), messageDto);
    }

//    @PostMapping("/api/messages/upload")
//    @ResponseBody
//    public ResponseEntity<String> uploadMedia(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("type") String type) {
//        String fileUrl = fileStorageService.storeFile(file, type);
//        return ResponseEntity.ok(fileUrl);
//    }
@PostMapping("/api/messages/upload")
@ResponseBody
public ResponseEntity<Map<String, String>> uploadMedia(
        @RequestParam("file") MultipartFile file,
        @RequestParam("type") String type) {

    String fileUrl = fileStorageService.storeFile(file, type);
    Map<String, String> response = new HashMap<>();
    response.put("url", fileUrl);
    return ResponseEntity.ok(response);
}



    @GetMapping("/api/messages/chat/{chatId}")
    @ResponseBody
    public ResponseEntity<List<MessageDto>> getChatMessages(@PathVariable Long chatId) {
        List<MessageDto> messages = messageService.getChatMessages(chatId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/api/messages/group/{groupId}")
    @ResponseBody
    public ResponseEntity<List<MessageDto>> getGroupMessages(@PathVariable Long groupId) {
        List<MessageDto> messages = messageService.getGroupMessages(groupId);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/api/messages/{messageId}")
    @ResponseBody
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }
}
