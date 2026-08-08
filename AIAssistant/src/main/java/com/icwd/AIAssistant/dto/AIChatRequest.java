package com.icwd.AIAssistant.dto;

import jakarta.validation.constraints.NotBlank;

public record AIChatRequest(

        @NotBlank(message = "Message cannot be empty")
        String message

) {
}