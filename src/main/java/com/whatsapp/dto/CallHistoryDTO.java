package com.whatsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallHistoryDTO {
    private Long id;
    private Long callerId;
    private String callerName;
    private String callerPhoto;
    private Long receiverId;
    private String receiverName;
    private String receiverPhoto;
    private String callType;
    private String callStatus;
    private Long callDuration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
