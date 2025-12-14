package com.mobile.infrafixapp.controller;

import com.mobile.infrafixapp.dto.request.ChatRequest;
import com.mobile.infrafixapp.dto.ChatResponse;
import com.mobile.infrafixapp.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.getChatResponse(request));
    }
}
