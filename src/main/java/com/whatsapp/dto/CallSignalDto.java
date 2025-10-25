package com.whatsapp.dto;

import lombok.Data;

@Data
public class CallSignalDto {
    private Long callId;
    private Long callerId;
    private Long receiverId;
    private Long groupId;
    private String callerName;
    private String callType; // AUDIO, VIDEO, GROUP_AUDIO, GROUP_VIDEO
    private String signalType; // OFFER, ANSWER, ICE_CANDIDATE
    private String sdp;
    private String candidate;
}
