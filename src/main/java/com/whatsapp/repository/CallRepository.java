package com.whatsapp.repository;

import com.whatsapp.model.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {
    @Query("SELECT c FROM Call c WHERE c.caller.id = :userId OR c.receiver.id = :userId " +
            "ORDER BY c.startTime DESC")
    List<Call> findCallHistoryByUserId(Long userId);
}
