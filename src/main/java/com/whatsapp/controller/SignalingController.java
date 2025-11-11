package com.whatsapp.controller;


import com.whatsapp.dto.SignalingMessage;
import com.whatsapp.model.CallHistory;
import com.whatsapp.repository.CallRepository;
import com.whatsapp.service.CallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SignalingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CallService callService;
    private final CallRepository callHistoryRepository;

    @MessageMapping("/call.request")
    public void handleCallRequest(@Payload SignalingMessage message) {
        log.info("📞 CALL_REQUEST from {} to {}", message.getSenderId(), message.getReceiverId());
        log.info("   Call Type: {}", message.getCallType());

        // ✅ Create call history record
        try {
            CallHistory callHistory = callService.createCallHistory(
                    message.getSenderId(),
                    message.getReceiverId(),
                    message.getCallType()
            );
            log.info("📝 Call history created: ID={}", callHistory.getId());
        } catch (Exception e) {
            log.error("❌ Failed to create call history", e);
        }

        String destination = "/topic/user/" + message.getReceiverId() + "/call";
        log.info("📤 Broadcasting to: {}", destination);

        messagingTemplate.convertAndSend(destination, message);

        log.info("✅ Message sent successfully");
    }

    @MessageMapping("/call.accept")
    public void handleCallAccept(@Payload SignalingMessage message) {
        log.info("✅ CALL_ACCEPT from {} to {}", message.getSenderId(), message.getReceiverId());

        // ✅ Update call status to ACCEPTED
        try {
            Optional<CallHistory> callOpt = callHistoryRepository.findOngoingCallBetweenUsers(
                    message.getSenderId(),
                    message.getReceiverId()
            );

            if (callOpt.isPresent()) {
                CallHistory call = callOpt.get();
                call.setCallStatus("ACCEPTED");
                callHistoryRepository.save(call);
                log.info("✅ Call status updated to ACCEPTED: ID={}", call.getId());
            } else {
                log.warn("⚠️ No ongoing call found to accept");
            }
        } catch (Exception e) {
            log.error("❌ Failed to update call status", e);
        }

        String destination = "/topic/user/" + message.getReceiverId() + "/call";
        log.info("📤 Sending to: {}", destination);

        messagingTemplate.convertAndSend(destination, message);

        log.info("✅ Call accept sent");
    }

    @MessageMapping("/call.reject")
    public void handleCallReject(@Payload SignalingMessage message) {
        log.info("❌ CALL_REJECT from {} to {}", message.getSenderId(), message.getReceiverId());

        // ✅ Update call status to REJECTED
        try {
            Optional<CallHistory> callOpt = callHistoryRepository.findOngoingCallBetweenUsers(
                    message.getSenderId(),
                    message.getReceiverId()
            );

            if (callOpt.isPresent()) {
                CallHistory call = callOpt.get();
                call.setCallStatus("REJECTED");
                call.setEndTime(LocalDateTime.now());
                call.setCallDuration(0L);
                callHistoryRepository.save(call);
                log.info("❌ Call status updated to REJECTED: ID={}", call.getId());
            } else {
                log.warn("⚠️ No ongoing call found to reject");
            }
        } catch (Exception e) {
            log.error("❌ Failed to update call status", e);
        }

        String destination = "/topic/user/" + message.getReceiverId() + "/call";
        log.info("📤 Sending to: {}", destination);

        messagingTemplate.convertAndSend(destination, message);

        log.info("✅ Call reject sent");
    }

    @MessageMapping("/call.end")
    public void handleCallEnd(@Payload SignalingMessage message) {
        log.info("📴 CALL_END from {} to {}", message.getSenderId(), message.getReceiverId());

        // ✅ Update call status to COMPLETED and calculate duration
        try {
            Optional<CallHistory> callOpt = callHistoryRepository.findOngoingCallBetweenUsers(
                    message.getSenderId(),
                    message.getReceiverId()
            );

            if (callOpt.isPresent()) {
                CallHistory call = callOpt.get();
                LocalDateTime endTime = LocalDateTime.now();
                call.setEndTime(endTime);

                // Calculate duration
                if (call.getStartTime() != null) {
                    long duration = java.time.Duration.between(call.getStartTime(), endTime).getSeconds();
                    call.setCallDuration(duration);
                }

                // Set final status
                if ("ACCEPTED".equals(call.getCallStatus())) {
                    call.setCallStatus("COMPLETED");
                } else {
                    call.setCallStatus("MISSED");
                }

                callHistoryRepository.save(call);
                log.info("📴 Call ended: ID={}, Duration={}s, Status={}",
                        call.getId(), call.getCallDuration(), call.getCallStatus());
            } else {
                log.warn("⚠️ No ongoing call found to end");
            }
        } catch (Exception e) {
            log.error("❌ Failed to update call end status", e);
        }

        String destination = "/topic/user/" + message.getReceiverId() + "/call";
        log.info("📤 Sending to: {}", destination);

        messagingTemplate.convertAndSend(destination, message);

        log.info("✅ Call end sent");
    }

    @MessageMapping("/offer")
    public void handleOffer(@Payload SignalingMessage message) {
        log.info("📤 OFFER from {} to {}", message.getSenderId(), message.getReceiverId());

        String destination = "/topic/user/" + message.getReceiverId() + "/call";
        messagingTemplate.convertAndSend(destination, message);

        log.info("✅ Offer sent");
    }

    @MessageMapping("/answer")
    public void handleAnswer(@Payload SignalingMessage message) {
        log.info("📥 ANSWER from {} to {}", message.getSenderId(), message.getReceiverId());

        String destination = "/topic/user/" + message.getReceiverId() + "/call";
        messagingTemplate.convertAndSend(destination, message);

        log.info("✅ Answer sent");
    }

    @MessageMapping("/ice.candidate")
    public void handleIceCandidate(@Payload SignalingMessage message) {
        log.info("🧊 ICE_CANDIDATE from {} to {}", message.getSenderId(), message.getReceiverId());

        String destination = "/topic/user/" + message.getReceiverId() + "/call";
        messagingTemplate.convertAndSend(destination, message);

        log.info("✅ ICE candidate sent");
    }
}

