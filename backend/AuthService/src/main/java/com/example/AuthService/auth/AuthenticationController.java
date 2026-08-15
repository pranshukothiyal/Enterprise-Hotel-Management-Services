package com.example.AuthService.auth;

import com.example.AuthService.entity.AuthenticationRequest;
import com.example.AuthService.entity.AuthenticationResponse;
import com.example.AuthService.entity.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        log.info(
                "Received user registration request. email={}",
                request.getEmail()
        );

        AuthenticationResponse response =
                authenticationService.register(request);

        log.info(
                "User registration completed successfully. email={}",
                request.getEmail()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {

        log.info(
                "Received authentication request. email={}",
                request.getEmail()
        );

        AuthenticationResponse response =
                authenticationService.authenticate(request);

        log.info(
                "Authentication completed successfully. email={}",
                request.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken() {

        log.debug(
                "Token validation endpoint reached after successful JWT validation"
        );

        return ResponseEntity.ok("Token is valid");
    }
}