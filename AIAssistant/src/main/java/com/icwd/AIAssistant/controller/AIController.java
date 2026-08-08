package com.icwd.AIAssistant.controller;

import com.icwd.AIAssistant.dto.AIChatRequest;
import com.icwd.AIAssistant.dto.AIChatResponse;
import com.icwd.AIAssistant.service.AIHelperService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIHelperService aiHelperService;

    public AIController(AIHelperService aiHelperService) {
        this.aiHelperService = aiHelperService;
    }

    @GetMapping("/ask")
    public ResponseEntity<AIChatResponse> ask(
            @RequestParam String message
    ) {
        AIChatRequest request = new AIChatRequest(message);

        return ResponseEntity.ok(
                aiHelperService.chat(request)
        );
    }

    @PostMapping("/chat")
    public ResponseEntity<AIChatResponse> chat(
            @Valid @RequestBody AIChatRequest request
    ) {
        AIChatResponse response = aiHelperService.chat(request);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok(
                "AI Assistant Service is running on port 8090"
        );
    }
}