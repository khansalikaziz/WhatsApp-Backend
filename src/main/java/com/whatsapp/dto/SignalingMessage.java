package com.whatsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalingMessage {
    private String type; // "OFFER", "ANSWER", "ICE_CANDIDATE", "CALL_REQUEST", "CALL_ACCEPT", "CALL_REJECT", "CALL_END", "CALL_BUSY"
    private Long senderId;
    private Long receiverId;
    private String callType; // "AUDIO" or "VIDEO"
    private Object data; // SDP or ICE candidate data
    private String senderName;
    private String senderPhoto;
}


