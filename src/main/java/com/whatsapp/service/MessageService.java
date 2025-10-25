package com.whatsapp.service;

import com.whatsapp.dto.MessageDto;
import com.whatsapp.model.Chat;
import com.whatsapp.model.Group;
import com.whatsapp.model.Message;
import com.whatsapp.model.User;
import com.whatsapp.repository.ChatRepository;
import com.whatsapp.repository.GroupRepository;
import com.whatsapp.repository.MessageRepository;
import com.whatsapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public MessageDto sendMessage(MessageDto messageDto) {
        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setContent(messageDto.getContent());
        message.setMessageType(messageDto.getMessageType());
        message.setMediaUrl(messageDto.getMediaUrl());
        message.setThumbnailUrl(messageDto.getThumbnailUrl());
        message.setMediaSize(messageDto.getMediaSize());
        message.setFileName(messageDto.getFileName());
        message.setStatus(Message.MessageStatus.SENT);

        if (messageDto.getChatId() != null) {
            Chat chat = chatRepository.findById(messageDto.getChatId())
                    .orElseThrow(() -> new RuntimeException("Chat not found"));
            message.setChat(chat);
            chat.setLastMessageTime(LocalDateTime.now());
            chatRepository.save(chat);
        } else if (messageDto.getGroupId() != null) {
            Group group = groupRepository.findById(messageDto.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            message.setGroup(group);
        }

        message = messageRepository.save(message);
        return mapToMessageDto(message);
    }

    public List<MessageDto> getChatMessages(Long chatId) {
        return messageRepository.findByChatIdOrderByTimestampAsc(chatId).stream()
                .map(this::mapToMessageDto)
                .collect(Collectors.toList());
    }

    public List<MessageDto> getGroupMessages(Long groupId) {
        return messageRepository.findByGroupIdOrderByTimestampAsc(groupId).stream()
                .map(this::mapToMessageDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateMessageStatus(Long messageId, Message.MessageStatus status) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setStatus(status);
        if (status == Message.MessageStatus.DELIVERED) {
            message.setDeliveredAt(LocalDateTime.now());
        } else if (status == Message.MessageStatus.READ) {
            message.setReadAt(LocalDateTime.now());
        }

        messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    private MessageDto mapToMessageDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getName());
        dto.setSenderProfilePicture(message.getSender().getProfilePicture());
        dto.setChatId(message.getChat() != null ? message.getChat().getId() : null);
        dto.setGroupId(message.getGroup() != null ? message.getGroup().getId() : null);
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setMediaUrl(message.getMediaUrl());
        dto.setThumbnailUrl(message.getThumbnailUrl());
        dto.setMediaSize(message.getMediaSize());
        dto.setFileName(message.getFileName());
        dto.setTimestamp(message.getTimestamp());
        dto.setStatus(message.getStatus());
        dto.setDeliveredAt(message.getDeliveredAt());
        dto.setReadAt(message.getReadAt());
        return dto;
    }
}
