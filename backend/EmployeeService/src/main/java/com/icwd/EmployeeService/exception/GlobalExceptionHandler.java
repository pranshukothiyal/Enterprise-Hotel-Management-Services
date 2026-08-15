package com.icwd.EmployeeService.exception;

import com.icwd.EmployeeService.payload.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(
            ResourceNotFoundException exception
    ) {

        log.warn(
                "Requested resource was not found. message={}",
                exception.getMessage()
        );

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiResponse> handleBadRequest(
            RuntimeException exception
    ) {

        log.warn(
                "Bad request rejected. exceptionType={}, message={}",
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {

        Map<String, String> errors =
                new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        log.warn(
                "Request validation failed. fieldErrorCount={}, fields={}",
                errors.size(),
                errors.keySet()
        );

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse>
    handleDataIntegrityViolation(
            DataIntegrityViolationException exception
    ) {

        log.warn(
                "Database integrity constraint violation occurred. exceptionType={}",
                exception.getClass().getSimpleName()
        );

        log.debug(
                "Database integrity violation details",
                exception
        );

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message(
                        "Duplicate or invalid database data"
                )
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(
            Exception exception
    ) {

        log.error(
                "Unhandled exception occurred in EmployeeService",
                exception
        );

        ApiResponse response = ApiResponse.builder()
                .success(false)
                .message("Internal server error")
                .data(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}