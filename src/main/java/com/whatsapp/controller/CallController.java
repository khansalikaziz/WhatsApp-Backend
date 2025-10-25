package com.whatsapp.controller;

import com.whatsapp.dto.CallSignalDto;
import com.whatsapp.model.Call;
import com.whatsapp.service.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CallController {

    private final CallService callService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/call.signal")
    public void handleCallSignal(@Payload CallSignalDto signalDto) {
        if ("OFFER".equals(signalDto.getSignalType())) {
            // Initiate call
            Call call = callService.initiateCall(signalDto);
            signalDto.setCallId(call.getId());

            // Send offer to receiver
            if (signalDto.getReceiverId() != null) {
                messagingTemplate.convertAndSendToUser(
                        signalDto.getReceiverId().toString(),
                        "/queue/call",
                        signalDto
                );
            } else if (signalDto.getGroupId() != null) {
                messagingTemplate.convertAndSend(
                        "/topic/group.call." + signalDto.getGroupId(),
                        signalDto
                );
            }
        } else if ("ANSWER".equals(signalDto.getSignalType())) {
            // Send answer back to caller
            messagingTemplate.convertAndSendToUser(
                    signalDto.getCallerId().toString(),
                    "/queue/call",
                    signalDto
            );
            callService.updateCallStatus(signalDto.getCallId(), Call.CallStatus.ONGOING);
        } else if ("ICE_CANDIDATE".equals(signalDto.getSignalType())) {
            // Forward ICE candidate
            Long targetUserId = signalDto.getReceiverId() != null ?
                    signalDto.getReceiverId() : signalDto.getCallerId();
            messagingTemplate.convertAndSendToUser(
                    targetUserId.toString(),
                    "/queue/call",
                    signalDto
            );
        }
    }

    @MessageMapping("/call.end")
    public void endCall(@Payload CallSignalDto signalDto) {
        callService.updateCallStatus(signalDto.getCallId(), Call.CallStatus.COMPLETED);

        // Notify other participant
        if (signalDto.getReceiverId() != null) {
            messagingTemplate.convertAndSendToUser(
                    signalDto.getReceiverId().toString(),
                    "/queue/call.end",
                    signalDto
            );
        }
    }

    @GetMapping("/api/calls/history/{userId}")
    @ResponseBody
    public ResponseEntity<List<Call>> getCallHistory(@PathVariable Long userId) {
        List<Call> history = callService.getCallHistory(userId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/api/calls/{callId}/status")
    @ResponseBody
    public ResponseEntity<Void> updateCallStatus(
            @PathVariable Long callId,
            @RequestParam String status) {
        callService.updateCallStatus(callId, Call.CallStatus.valueOf(status));
        return ResponseEntity.ok().build();
    }
}
