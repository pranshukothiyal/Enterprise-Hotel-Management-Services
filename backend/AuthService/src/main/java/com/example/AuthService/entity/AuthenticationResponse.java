package com.example.AuthService.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {


    // =========================================================
    // JWT
    // =========================================================

    private String token;


    // =========================================================
    // AUTH SERVICE ACCOUNT ID
    // =========================================================

    private String id;


    // =========================================================
    // LOGIN DETAILS
    // =========================================================

    private String email;

    private String role;


    // =========================================================
    // USER SERVICE LINK
    // =========================================================

    private String userId;


    // =========================================================
    // HOTEL SCOPE
    // =========================================================

    private String hotelId;


    // =========================================================
    // EMPLOYEE SERVICE LINK
    // =========================================================

    private String employeeId;


    // =========================================================
    // DEPARTMENT SCOPE
    // =========================================================

    private String departmentId;
}