package com.whatsapp.controller;



import com.whatsapp.dto.CallHistoryDTO;
import com.whatsapp.service.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class CallHistoryController {

    private final CallService callService;

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<CallHistoryDTO>> getUserCallHistory(@PathVariable Long userId) {
        List<CallHistoryDTO> callHistory = callService.getUserCallHistory(userId);
        return ResponseEntity.ok(callHistory);
    }

    @PutMapping("/history/{callId}/status")
    public ResponseEntity<Void> updateCallStatus(
            @PathVariable Long callId,
            @RequestParam String status,
            @RequestParam(required = false) String endTime) {

        callService.updateCallStatus(callId, status,
                endTime != null ? java.time.LocalDateTime.parse(endTime) : null);

        return ResponseEntity.ok().build();
    }
}

