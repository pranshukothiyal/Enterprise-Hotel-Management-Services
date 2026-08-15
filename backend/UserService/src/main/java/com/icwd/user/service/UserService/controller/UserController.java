package com.icwd.user.service.UserService.controller;

import com.icwd.user.service.UserService.entitites.User;
import com.icwd.user.service.UserService.services.UserServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


    // =========================================================
    // DEPENDENCY
    // =========================================================

    private final UserServices userService;


    // =========================================================
    // CREATE USER
    // POST /users
    // =========================================================

    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody User user
    ) {

        log.info(
                "Received request to create user. email={}",
                user.getEmail()
        );


        User savedUser =
                userService.saveUser(
                        user
                );


        log.info(
                "User created successfully. userId={}",
                savedUser.getUserId()
        );


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }


    // =========================================================
    // GET USER BY ID
    // GET /users/{userId}
    // =========================================================

    @GetMapping("/{userId}")
    public ResponseEntity<User> getSingleUser(
            @PathVariable String userId
    ) {

        log.info(
                "Received request to fetch user. userId={}",
                userId
        );


        User user =
                userService.getUser(
                        userId
                );


        return ResponseEntity.ok(
                user
        );
    }


    // =========================================================
    // GET USER BY EMAIL
    // GET /users/email/{email}
    // =========================================================

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(
            @PathVariable String email
    ) {

        log.info(
                "Received request to fetch user by email. email={}",
                email
        );


        User user =
                userService.getUserByEmail(
                        email
                );


        return ResponseEntity.ok(
                user
        );
    }


    // =========================================================
    // GET ALL USERS
    // GET /users
    // =========================================================

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        log.info(
                "Received request to fetch all users"
        );


        List<User> users =
                userService.getAllUsers();


        log.info(
                "Fetched users successfully. count={}",
                users.size()
        );


        return ResponseEntity.ok(
                users
        );
    }
}