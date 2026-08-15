package com.example.AuthService.service;

import com.example.AuthService.dto.AccountResponse;
import com.example.AuthService.dto.AssignHotelRequest;

import com.example.AuthService.entity.Role;
import com.example.AuthService.entity.User;

import com.example.AuthService.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountManagementService {


    private final UserRepository userRepository;


    // =========================================================
    // GET ALL AUTH ACCOUNTS
    // ADMIN ONLY
    // =========================================================

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {


        return userRepository
                .findAll()
                .stream()
                .map(
                        this::toResponse
                )
                .toList();
    }


    // =========================================================
    // GET ALL MANAGER ACCOUNTS
    // ADMIN ONLY
    // =========================================================

    @Transactional(readOnly = true)
    public List<AccountResponse> getManagers() {


        return userRepository
                .findAll()
                .stream()

                .filter(
                        user ->
                                user.getRole()
                                        == Role.HOTEL_MANAGER
                )

                .map(
                        this::toResponse
                )

                .toList();
    }


    // =========================================================
    // GET ACCOUNT BY AUTH ID
    // ADMIN ONLY
    // =========================================================

    @Transactional(readOnly = true)
    public AccountResponse getAccount(
            String authUserId
    ) {


        User user =
                findAccount(
                        authUserId
                );


        return toResponse(
                user
        );
    }


    // =========================================================
    // ASSIGN HOTEL TO MANAGER
    // =========================================================

    @Transactional
    public AccountResponse assignHotel(
            String authUserId,
            AssignHotelRequest request
    ) {


        // =====================================================
        // VALIDATE REQUEST
        // =====================================================

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hotel assignment request is required"
            );
        }


        if (
                request.getHotelId() == null
                        ||
                        request.getHotelId().isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "hotelId is required"
            );
        }


        String hotelId =
                request
                        .getHotelId()
                        .trim();


        // =====================================================
        // FIND AUTH ACCOUNT
        // =====================================================

        User manager =
                findAccount(
                        authUserId
                );


        // =====================================================
        // VERIFY ROLE
        // =====================================================

        if (
                manager.getRole()
                        != Role.HOTEL_MANAGER
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hotel can only be assigned to a HOTEL_MANAGER account"
            );
        }


        // =====================================================
        // ASSIGN HOTEL
        // =====================================================

        manager.setHotelId(
                hotelId
        );


        User savedManager =
                userRepository.save(
                        manager
                );


        log.info(
                "Hotel assigned to manager. authUserId={}, email={}, hotelId={}",
                savedManager.getId(),
                savedManager.getEmail(),
                hotelId
        );


        return toResponse(
                savedManager
        );
    }


    // =========================================================
    // REMOVE HOTEL ASSIGNMENT
    // =========================================================

    @Transactional
    public AccountResponse removeHotelAssignment(
            String authUserId
    ) {


        User manager =
                findAccount(
                        authUserId
                );


        if (
                manager.getRole()
                        != Role.HOTEL_MANAGER
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Hotel assignment exists only for HOTEL_MANAGER accounts"
            );
        }


        manager.setHotelId(
                null
        );


        User savedManager =
                userRepository.save(
                        manager
                );


        log.info(
                "Hotel assignment removed. authUserId={}, email={}",
                savedManager.getId(),
                savedManager.getEmail()
        );


        return toResponse(
                savedManager
        );
    }


    // =========================================================
    // FIND ACCOUNT
    // =========================================================

    private User findAccount(
            String authUserId
    ) {


        if (
                authUserId == null
                        ||
                        authUserId.isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Auth user ID is required"
            );
        }


        return userRepository
                .findById(
                        authUserId
                )
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Auth account not found"
                                )
                );
    }


    // =========================================================
    // ENTITY -> RESPONSE
    // =========================================================

    private AccountResponse toResponse(
            User user
    ) {


        return new AccountResponse(

                user.getId(),

                user.getFirstName(),

                user.getLastName(),

                user.getEmail(),

                user.getRole() != null
                        ? user.getRole().name()
                        : null,

                user.getUserId(),

                user.getHotelId(),

                user.getEmployeeId(),

                user.getDepartmentId()
        );
    }
}