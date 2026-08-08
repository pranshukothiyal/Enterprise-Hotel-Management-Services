package com.icwd.AIAssistant.service;

import com.icwd.AIAssistant.dto.AIChatRequest;
import com.icwd.AIAssistant.dto.AIChatResponse;
import com.icwd.AIAssistant.exception.AIAssistantException;
import jakarta.validation.Valid;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIHelperService {

    private final ChatClient chatClient;

    public AIHelperService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public AIChatResponse chat(@Valid AIChatRequest request) {

        try {
            String answer = chatClient
                    .prompt()
                    .user(request.message())
                    .call()
                    .content();

            if (answer == null || answer.isBlank()) {
                answer = "The AI assistant returned an empty response.";
            }

            return new AIChatResponse(
                    "llama3.2",
                    answer
            );

        } catch (Exception exception) {
            throw new AIAssistantException(
                    """
                    AI assistant is unavailable. Confirm that
                    Ollama, Eureka and HotelService are running.
                    """,
                    exception
            );
        }
    }
}