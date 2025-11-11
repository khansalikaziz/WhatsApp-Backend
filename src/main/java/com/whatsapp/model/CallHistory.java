package com.whatsapp.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "call_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "caller_id", nullable = false)
    private Long callerId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "call_type", nullable = false)
    private String callType; // "AUDIO" or "VIDEO"

    @Column(name = "call_status", nullable = false)
    private String callStatus; // "MISSED", "ANSWERED", "REJECTED", "CANCELLED"

    @Column(name = "call_duration")
    private Long callDuration; // in seconds

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

