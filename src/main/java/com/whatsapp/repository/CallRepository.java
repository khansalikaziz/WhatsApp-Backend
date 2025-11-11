package com.whatsapp.repository;

import com.whatsapp.model.Call;
import com.whatsapp.model.CallHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CallRepository extends JpaRepository<CallHistory, Long> {
    @Query("SELECT c FROM CallHistory c WHERE " +
            "((c.callerId = ?1 AND c.receiverId = ?2) OR (c.callerId = ?2 AND c.receiverId = ?1)) " +
            "AND c.endTime IS NULL " +
            "ORDER BY c.startTime DESC")
    Optional<CallHistory> findOngoingCallBetweenUsers(Long user1Id, Long user2Id);

    @Query("SELECT c FROM CallHistory c WHERE c.callerId = ?1 OR c.receiverId = ?1 ORDER BY c.startTime DESC")
    List<CallHistory> findCallHistoryByUserId(Long userId);

    @Query("SELECT c FROM CallHistory c WHERE (c.callerId = ?1 AND c.receiverId = ?2) OR (c.callerId = ?2 AND c.receiverId = ?1) ORDER BY c.startTime DESC")
    List<CallHistory> findCallHistoryBetweenUsers(Long userId1, Long userId2);
}
