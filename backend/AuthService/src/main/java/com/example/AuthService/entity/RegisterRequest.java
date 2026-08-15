package com.example.AuthService.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {


    // =========================================================
    // FIRST NAME
    // =========================================================

    @NotBlank(
            message = "First name is required"
    )
    private String firstName;


    // =========================================================
    // LAST NAME
    // =========================================================

    @NotBlank(
            message = "Last name is required"
    )
    private String lastName;


    // =========================================================
    // EMAIL
    // =========================================================

    @NotBlank(
            message = "Email is required"
    )
    @Email(
            message = "Enter a valid email address"
    )
    private String email;


    // =========================================================
    // PASSWORD
    // =========================================================

    @NotBlank(
            message = "Password is required"
    )
    @Size(
            min = 8,
            message =
                    "Password must contain at least 8 characters"
    )
    private String password;


    // =========================================================
    // ROLE SELECTED FROM REACT
    // =========================================================

    private Role role;
}