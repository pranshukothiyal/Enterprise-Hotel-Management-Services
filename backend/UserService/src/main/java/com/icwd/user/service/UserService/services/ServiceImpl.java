package com.icwd.user.service.UserService.services;

import com.icwd.user.service.UserService.entitites.User;
import com.icwd.user.service.UserService.exception.ResourceNotFoundException;
import com.icwd.user.service.UserService.repositories.UserRepositories;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ServiceImpl implements UserServices {


    // =========================================================
    // DEPENDENCY
    // =========================================================

    private final UserRepositories userRepositories;


    // =========================================================
    // CREATE USER
    // =========================================================

    @Override
    public User saveUser(
            User user
    ) {

        // =====================================================
        // VALIDATE USER
        // =====================================================

        if (user == null) {

            throw new IllegalArgumentException(
                    "User is required"
            );
        }


        if (
                user.getEmail() == null
                        ||
                        user.getEmail().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Email is required"
            );
        }


        // =====================================================
        // NORMALIZE EMAIL
        // =====================================================

        String normalizedEmail =
                user
                        .getEmail()
                        .trim()
                        .toLowerCase();


        user.setEmail(
                normalizedEmail
        );


        // =====================================================
        // CHECK EXISTING USER BY EMAIL
        // =====================================================

        User existingUser =
                userRepositories
                        .findByEmailIgnoreCase(
                                normalizedEmail
                        )
                        .orElse(null);


        /*
         * If AuthService calls UserService again for the same
         * registered email, do not create a duplicate profile.
         *
         * Return the existing UserService profile.
         */
        if (existingUser != null) {

            log.info(
                    "User profile already exists. email={}, userId={}",
                    normalizedEmail,
                    existingUser.getUserId()
            );


            return existingUser;
        }


        // =====================================================
        // GENERATE USER ID
        // =====================================================

        String randomUserId =
                UUID
                        .randomUUID()
                        .toString();


        user.setUserId(
                randomUserId
        );


        log.debug(
                "Generated UserService userId={}",
                randomUserId
        );


        // =====================================================
        // SAVE USER
        // =====================================================

        User savedUser =
                userRepositories.save(
                        user
                );


        log.info(
                "User created successfully. userId={}, email={}",
                savedUser.getUserId(),
                savedUser.getEmail()
        );


        return savedUser;
    }


    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Override
    public List<User> getAllUsers() {

        log.debug(
                "Fetching all UserService users"
        );


        List<User> users =
                userRepositories.findAll();


        log.info(
                "Users fetched successfully. count={}",
                users.size()
        );


        return users;
    }


    // =========================================================
    // GET USER BY ID
    // =========================================================

    @Override
    public User getUser(
            String userId
    ) {

        log.debug(
                "Fetching user by ID. userId={}",
                userId
        );


        return userRepositories
                .findById(
                        userId
                )
                .orElseThrow(
                        () -> {

                            log.warn(
                                    "User not found. userId={}",
                                    userId
                            );


                            return new ResourceNotFoundException(
                                    "User with given id is not found on server"
                            );
                        }
                );
    }


    // =========================================================
    // GET USER BY EMAIL
    // =========================================================

    @Override
    public User getUserByEmail(
            String email
    ) {

        if (
                email == null
                        ||
                        email.isBlank()
        ) {

            throw new ResourceNotFoundException(
                    "Email is required"
            );
        }


        String normalizedEmail =
                email
                        .trim()
                        .toLowerCase();


        log.debug(
                "Fetching user by email. email={}",
                normalizedEmail
        );


        return userRepositories
                .findByEmailIgnoreCase(
                        normalizedEmail
                )
                .orElseThrow(
                        () -> {

                            log.warn(
                                    "User not found for email={}",
                                    normalizedEmail
                            );


                            return new ResourceNotFoundException(
                                    "User with email "
                                            + normalizedEmail
                                            + " is not found on server"
                            );
                        }
                );
    }
}