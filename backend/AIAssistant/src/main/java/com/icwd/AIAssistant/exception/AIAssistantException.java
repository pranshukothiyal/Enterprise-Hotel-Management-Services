package com.icwd.AIAssistant.exception;

public class AIAssistantException extends RuntimeException {

    public AIAssistantException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}