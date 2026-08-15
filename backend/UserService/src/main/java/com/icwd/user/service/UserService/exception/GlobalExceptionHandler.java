package com.icwd.user.service.UserService.exception;

import com.icwd.user.service.UserService.payload.ApiResponse;
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
             ResourceNotFoundException ex
     ) {

          log.warn(
                  "Requested user resource was not found. message={}",
                  ex.getMessage()
          );

          String message = ex.getMessage();

          ApiResponse response =
                  ApiResponse.builder()
                          .message(message)
                          .success(true)
                          .status(HttpStatus.NOT_FOUND)
                          .build();

          return new ResponseEntity<ApiResponse>(
                  response,
                  HttpStatus.NOT_FOUND
          );
     }
}