package com.whatsapp.service;


import com.whatsapp.dto.CallHistoryDTO;
import com.whatsapp.model.CallHistory;
import com.whatsapp.model.User;
import com.whatsapp.repository.CallRepository;
import com.whatsapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository callHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CallHistory createCallHistory(Long callerId, Long receiverId, String callType) {
        CallHistory callHistory = CallHistory.builder()
                .callerId(callerId)
                .receiverId(receiverId)
                .callType(callType)
                .callStatus("INITIATED")
                .startTime(LocalDateTime.now())
                .build();

        return callHistoryRepository.save(callHistory);
    }

    @Transactional
    public void updateCallStatus(Long callHistoryId, String status, LocalDateTime endTime) {
        CallHistory callHistory = callHistoryRepository.findById(callHistoryId)
                .orElseThrow(() -> new RuntimeException("Call history not found"));

        callHistory.setCallStatus(status);
        callHistory.setEndTime(endTime);

        if (endTime != null && callHistory.getStartTime() != null) {
            Duration duration = Duration.between(callHistory.getStartTime(), endTime);
            callHistory.setCallDuration(duration.getSeconds());
        }

        callHistoryRepository.save(callHistory);
    }

    public List<CallHistoryDTO> getUserCallHistory(Long userId) {
        List<CallHistory> callHistories = callHistoryRepository.findCallHistoryByUserId(userId);

        return callHistories.stream().map(this::convertToDTO).collect(Collectors.toList());
    }



    private CallHistoryDTO convertToDTO(CallHistory callHistory) {
        User caller = userRepository.findById(callHistory.getCallerId()).orElse(null);
        User receiver = userRepository.findById(callHistory.getReceiverId()).orElse(null);

        return CallHistoryDTO.builder()
                .id(callHistory.getId())
                .callerId(callHistory.getCallerId())
                .callerName(caller != null ? caller.getName() : "Unknown")
                .callerPhoto(null)
                .receiverId(callHistory.getReceiverId())
                .receiverName(receiver != null ? receiver.getName() : "Unknown")
                .receiverPhoto(null)
                .callType(callHistory.getCallType())
                .callStatus(callHistory.getCallStatus())
                .callDuration(callHistory.getCallDuration())
                .startTime(callHistory.getStartTime())
                .endTime(callHistory.getEndTime())
                .build();
    }


}


