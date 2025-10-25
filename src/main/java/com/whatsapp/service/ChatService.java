package com.whatsapp.service;

import com.whatsapp.dto.ChatDto;
import com.whatsapp.model.Chat;
import com.whatsapp.model.Message;
import com.whatsapp.model.User;
import com.whatsapp.repository.ChatRepository;
import com.whatsapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatDto createOrGetChat(Long user1Id, Long user2Id) {
        return chatRepository.findChatBetweenUsers(user1Id, user2Id)
                .map(chat -> mapToChatDto(chat, user1Id))
                .orElseGet(() -> {
                    User user1 = userRepository.findById(user1Id)
                            .orElseThrow(() -> new RuntimeException("User1 not found"));
                    User user2 = userRepository.findById(user2Id)
                            .orElseThrow(() -> new RuntimeException("User2 not found"));

                    Chat chat = new Chat();
                    chat.setUser1(user1);
                    chat.setUser2(user2);
                    chat = chatRepository.save(chat);

                    return mapToChatDto(chat, user1Id);
                });
    }

    public List<ChatDto> getUserChats(Long userId) {
        return chatRepository.findChatsByUserId(userId).stream()
                .map(chat -> mapToChatDto(chat, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteChat(Long chatId) {
        chatRepository.deleteById(chatId);
    }

    private ChatDto mapToChatDto(Chat chat, Long currentUserId) {
        User otherUser = chat.getUser1().getId().equals(currentUserId)
                ? chat.getUser2() : chat.getUser1();

        ChatDto dto = new ChatDto();
        dto.setId(chat.getId());
        dto.setUserId(otherUser.getId());
        dto.setUserName(otherUser.getName());
        dto.setUserProfilePicture(otherUser.getProfilePicture());
        dto.setOnline(otherUser.isOnline());
        dto.setLastSeen(otherUser.getLastSeen());

        if (!chat.getMessages().isEmpty()) {
            Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);
            dto.setLastMessage(lastMessage.getContent() != null ? lastMessage.getContent() :
                    lastMessage.getMessageType().toString());
            dto.setLastMessageTime(lastMessage.getTimestamp());
        }

        // Calculate unread count
        long unreadCount = chat.getMessages().stream()
                .filter(m -> !m.getSender().getId().equals(currentUserId)
                        && m.getStatus() != Message.MessageStatus.READ)
                .count();
        dto.setUnreadCount((int) unreadCount);

        return dto;
    }
}
