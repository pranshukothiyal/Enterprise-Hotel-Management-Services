package com.example.AuthService.client;

import com.example.AuthService.dto.UserProfileRequest;
import com.example.AuthService.dto.UserProfileResponse;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(
        name = "USER-SERVICE"
)
public interface UserServiceClient {


    @PostMapping("/users")
    UserProfileResponse createUser(
            @RequestBody UserProfileRequest request
    );


    @GetMapping("/users/email/{email}")
    UserProfileResponse getUserByEmail(
            @PathVariable("email") String email
    );
}