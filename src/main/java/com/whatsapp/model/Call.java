package com.whatsapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "calls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id")
    private User caller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private CallType callType;

    @Enumerated(EnumType.STRING)
    private CallStatus callStatus;

    @CreationTimestamp
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long duration; // in seconds

    public enum CallType {
        AUDIO, VIDEO, GROUP_AUDIO, GROUP_VIDEO
    }

    public enum CallStatus {
        INITIATED, RINGING, ONGOING, COMPLETED, MISSED, DECLINED, FAILED
    }
}
