package com.whatsapp.service;

import com.whatsapp.dto.CallSignalDto;
import com.whatsapp.model.Call;
import com.whatsapp.model.Group;
import com.whatsapp.model.User;
import com.whatsapp.repository.CallRepository;
import com.whatsapp.repository.GroupRepository;
import com.whatsapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallService {

    private final CallRepository callRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public Call initiateCall(CallSignalDto signalDto) {
        User caller = userRepository.findById(signalDto.getCallerId())
                .orElseThrow(() -> new RuntimeException("Caller not found"));

        Call call = new Call();
        call.setCaller(caller);
        call.setCallStatus(Call.CallStatus.INITIATED);

        if (signalDto.getCallType().contains("GROUP")) {
            Group group = groupRepository.findById(signalDto.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            call.setGroup(group);
            call.setCallType(Call.CallType.valueOf(signalDto.getCallType()));
        } else {
            User receiver = userRepository.findById(signalDto.getReceiverId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));
            call.setReceiver(receiver);
            call.setCallType(Call.CallType.valueOf(signalDto.getCallType()));
        }

        return callRepository.save(call);
    }

    @Transactional
    public void updateCallStatus(Long callId, Call.CallStatus status) {
        Call call = callRepository.findById(callId)
                .orElseThrow(() -> new RuntimeException("Call not found"));

        call.setCallStatus(status);

        if (status == Call.CallStatus.COMPLETED ||
                status == Call.CallStatus.MISSED ||
                status == Call.CallStatus.DECLINED ||
                status == Call.CallStatus.FAILED) {
            call.setEndTime(LocalDateTime.now());
            if (call.getStartTime() != null) {
                long duration = ChronoUnit.SECONDS.between(call.getStartTime(), call.getEndTime());
                call.setDuration(duration);
            }
        } else if (status == Call.CallStatus.ONGOING && call.getStartTime() == null) {
            call.setStartTime(LocalDateTime.now());
        }

        callRepository.save(call);
    }

    public List<Call> getCallHistory(Long userId) {
        return callRepository.findCallHistoryByUserId(userId);
    }

    public Call getCallById(Long callId) {
        return callRepository.findById(callId)
                .orElseThrow(() -> new RuntimeException("Call not found"));
    }
}
