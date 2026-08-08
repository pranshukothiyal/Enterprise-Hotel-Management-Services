package com.icwd.hotel.exception;

import com.icwd.hotel.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // Crucial: Tells Spring this class handles global exceptions
public class GlobalExceptionalHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> notFoundHandler(ResourceNotFoundException ex) {

        // 1. Build the ApiResponse object matching your method's generic type
        ApiResponse response = ApiResponse.builder()
                .message(ex.getMessage())
                .success(false)
                .status(HttpStatus.NOT_FOUND)
                .build();

        // 2. Return the ResponseEntity containing the ApiResponse object
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}