package com.whatsapp.repository;

import com.whatsapp.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT c FROM Chat c WHERE (c.user1.id = :user1Id AND c.user2.id = :user2Id) " +
            "OR (c.user1.id = :user2Id AND c.user2.id = :user1Id)")
    Optional<Chat> findChatBetweenUsers(Long user1Id, Long user2Id);

    @Query("SELECT c FROM Chat c WHERE c.user1.id = :userId OR c.user2.id = :userId")
    List<Chat> findChatsByUserId(Long userId);
}
