package com.example.AuthService.controller;

import com.example.AuthService.dto.AccountResponse;
import com.example.AuthService.dto.AssignHotelRequest;

import com.example.AuthService.service.AccountManagementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountManagementController {


    private final AccountManagementService
            accountManagementService;


    // =========================================================
    // GET ALL ACCOUNTS
    // =========================================================
    //
    // GET /api/v1/accounts
    //
    // ADMIN ONLY
    //
    // =========================================================

    @GetMapping
    public ResponseEntity<List<AccountResponse>>
    getAllAccounts() {


        log.info(
                "Admin requested all AuthService accounts"
        );


        return ResponseEntity.ok(

                accountManagementService
                        .getAllAccounts()
        );
    }


    // =========================================================
    // GET HOTEL MANAGERS
    // =========================================================
    //
    // GET /api/v1/accounts/managers
    //
    // ADMIN ONLY
    //
    // =========================================================

    @GetMapping("/managers")
    public ResponseEntity<List<AccountResponse>>
    getManagers() {


        log.info(
                "Admin requested Hotel Manager accounts"
        );


        return ResponseEntity.ok(

                accountManagementService
                        .getManagers()
        );
    }


    // =========================================================
    // GET ACCOUNT
    // =========================================================
    //
    // GET /api/v1/accounts/{authUserId}
    //
    // ADMIN ONLY
    //
    // =========================================================

    @GetMapping("/{authUserId}")
    public ResponseEntity<AccountResponse>
    getAccount(
            @PathVariable String authUserId
    ) {


        return ResponseEntity.ok(

                accountManagementService
                        .getAccount(
                                authUserId
                        )
        );
    }


    // =========================================================
    // ASSIGN HOTEL
    // =========================================================
    //
    // PATCH /api/v1/accounts/{authUserId}/hotel
    //
    // Body:
    //
    // {
    //     "hotelId": "HTL-001"
    // }
    //
    // ADMIN ONLY
    //
    // =========================================================

    @PatchMapping("/{authUserId}/hotel")
    public ResponseEntity<AccountResponse>
    assignHotel(
            @PathVariable String authUserId,
            @RequestBody AssignHotelRequest request
    ) {


        log.info(
                "Received manager hotel assignment. authUserId={}, hotelId={}",
                authUserId,
                request != null
                        ? request.getHotelId()
                        : null
        );


        return ResponseEntity.ok(

                accountManagementService
                        .assignHotel(
                                authUserId,
                                request
                        )
        );
    }


    // =========================================================
    // REMOVE HOTEL ASSIGNMENT
    // =========================================================
    //
    // DELETE /api/v1/accounts/{authUserId}/hotel
    //
    // ADMIN ONLY
    //
    // =========================================================

    @DeleteMapping("/{authUserId}/hotel")
    public ResponseEntity<AccountResponse>
    removeHotelAssignment(
            @PathVariable String authUserId
    ) {


        log.info(
                "Received remove hotel assignment request. authUserId={}",
                authUserId
        );


        return ResponseEntity.ok(

                accountManagementService
                        .removeHotelAssignment(
                                authUserId
                        )
        );
    }
}