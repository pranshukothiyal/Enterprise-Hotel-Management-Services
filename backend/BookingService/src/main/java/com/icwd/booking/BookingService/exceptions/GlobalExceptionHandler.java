package com.icwd.booking.BookingService.exceptions;

import com.icwd.booking.BookingService.payload.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(
            ResourceNotFoundException exception
    ) {

        log.warn(
                "Resource not found. message={}",
                exception.getMessage()
        );

        ApiResponse response = new ApiResponse(
                exception.getMessage(),
                false
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {

        log.warn(
                "Invalid request. message={}",
                exception.getMessage()
        );

        ApiResponse response = new ApiResponse(
                exception.getMessage(),
                false
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(
            Exception exception
    ) {

        log.error(
                "Unhandled exception occurred",
                exception
        );

        ApiResponse response = new ApiResponse(
                exception.getMessage(),
                false
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}