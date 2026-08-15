package com.example.AuthService.auth;

import com.example.AuthService.client.UserServiceClient;
import com.example.AuthService.config.JwtService;

import com.example.AuthService.dto.UserProfileRequest;
import com.example.AuthService.dto.UserProfileResponse;

import com.example.AuthService.entity.AuthenticationRequest;
import com.example.AuthService.entity.AuthenticationResponse;
import com.example.AuthService.entity.RegisterRequest;
import com.example.AuthService.entity.Role;
import com.example.AuthService.entity.User;

import com.example.AuthService.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.AuthenticationException;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class AuthenticationService {


    // =========================================================
    // DEPENDENCIES
    // =========================================================

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserServiceClient userServiceClient;


    // =========================================================
    // REGISTER
    // =========================================================

    @Transactional
    public AuthenticationResponse register(
            RegisterRequest request
    ) {


        // =====================================================
        // VALIDATE REQUEST
        // =====================================================

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Registration request is required"
            );
        }


        if (
                request.getEmail() == null
                        ||
                        request.getEmail().isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required"
            );
        }


        if (
                request.getPassword() == null
                        ||
                        request.getPassword().isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password is required"
            );
        }


        // =====================================================
        // EMAIL
        // =====================================================

        String email =
                request
                        .getEmail()
                        .trim()
                        .toLowerCase();


        // =====================================================
        // DUPLICATE AUTH ACCOUNT
        // =====================================================

        if (
                userRepository
                        .existsByEmail(
                                email
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }


        // =====================================================
        // ROLE
        // =====================================================

        Role selectedRole =
                request.getRole() != null
                        ? request.getRole()
                        : Role.GUEST;


        // =====================================================
        // NAME
        // =====================================================

        String firstName =
                request.getFirstName() == null
                        ? ""
                        : request
                        .getFirstName()
                        .trim();


        String lastName =
                request.getLastName() == null
                        ? ""
                        : request
                        .getLastName()
                        .trim();


        // =====================================================
        // CREATE AUTH USER
        // =====================================================

        User user =
                User
                        .builder()

                        .firstName(
                                firstName
                        )

                        .lastName(
                                lastName
                        )

                        .email(
                                email
                        )

                        .password(
                                passwordEncoder
                                        .encode(
                                                request
                                                        .getPassword()
                                        )
                        )

                        .role(
                                selectedRole
                        )

                        .userId(
                                null
                        )

                        .hotelId(
                                null
                        )

                        .employeeId(
                                null
                        )

                        .departmentId(
                                null
                        )

                        .build();


        // =====================================================
        // CREATE GUEST PROFILE
        // =====================================================

        if (
                selectedRole == Role.GUEST
        ) {

            String userId =
                    createGuestProfile(
                            firstName,
                            lastName,
                            email
                    );


            user.setUserId(
                    userId
            );
        }


        // =====================================================
        // SAVE AUTH USER
        // =====================================================

        User savedUser =
                userRepository
                        .save(
                                user
                        );


        // =====================================================
        // JWT
        // =====================================================

        String token =
                jwtService
                        .generateToken(
                                savedUser
                        );


        return buildAuthenticationResponse(
                savedUser,
                token
        );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @Transactional
    public AuthenticationResponse authenticate(
            AuthenticationRequest request
    ) {


        // =====================================================
        // VALIDATE
        // =====================================================

        if (request == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Authentication request is required"
            );
        }


        if (
                request.getEmail() == null
                        ||
                        request.getEmail().isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required"
            );
        }


        if (
                request.getPassword() == null
                        ||
                        request.getPassword().isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password is required"
            );
        }


        String email =
                request
                        .getEmail()
                        .trim()
                        .toLowerCase();


        // =====================================================
        // PASSWORD AUTHENTICATION
        // =====================================================

        try {

            authenticationManager
                    .authenticate(

                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    request.getPassword()
                            )
                    );


        } catch (
                AuthenticationException exception
        ) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }


        // =====================================================
        // GET AUTH USER
        // =====================================================

        User user =
                userRepository
                        .findByEmail(
                                email
                        )

                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "User not found"
                                        )
                        );


        // =====================================================
        // ROLE CHECK
        // =====================================================

        if (
                request.getExpectedRole() != null
                        &&
                        user.getRole()
                                != request.getExpectedRole()
        ) {

            throw new ResponseStatusException(

                    HttpStatus.FORBIDDEN,

                    "This account is not authorized for the selected "
                            +
                            request
                                    .getExpectedRole()
                                    .name()
                            +
                            " portal"
            );
        }


        // =====================================================
        // OLD GUEST WITHOUT USER ID
        // =====================================================

        if (
                user.getRole() == Role.GUEST
                        &&
                        (
                                user.getUserId() == null
                                        ||
                                        user.getUserId().isBlank()
                        )
        ) {

            String userId =
                    findOrCreateGuestProfile(
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail()
                    );


            user.setUserId(
                    userId
            );


            user =
                    userRepository
                            .save(
                                    user
                            );
        }


        // =====================================================
        // JWT
        // =====================================================

        String token =
                jwtService
                        .generateToken(
                                user
                        );


        return buildAuthenticationResponse(
                user,
                token
        );
    }


    // =========================================================
    // CREATE GUEST PROFILE
    // =========================================================

    private String createGuestProfile(
            String firstName,
            String lastName,
            String email
    ) {


        String fullName =
                createFullName(
                        firstName,
                        lastName,
                        email
                );


        UserProfileRequest request =
                new UserProfileRequest(
                        fullName,
                        email,
                        "Guest profile"
                );


        try {

            UserProfileResponse response =
                    userServiceClient
                            .createUser(
                                    request
                            );


            return extractUserId(
                    response
            );


        } catch (
                Exception exception
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to create Guest profile in UserService"
            );
        }
    }


    // =========================================================
    // FIND OR CREATE EXISTING GUEST PROFILE
    // =========================================================

    private String findOrCreateGuestProfile(
            String firstName,
            String lastName,
            String email
    ) {


        try {

            UserProfileResponse existing =
                    userServiceClient
                            .getUserByEmail(
                                    email
                            );


            if (
                    existing != null
                            &&
                            existing.getUserId() != null
                            &&
                            !existing
                                    .getUserId()
                                    .isBlank()
            ) {

                return existing
                        .getUserId();
            }


        } catch (
                Exception ignored
        ) {

            /*
             * If profile lookup fails because the profile
             * does not exist, try creating it.
             */
        }


        return createGuestProfile(
                firstName,
                lastName,
                email
        );
    }


    // =========================================================
    // FULL NAME
    // =========================================================

    private String createFullName(
            String firstName,
            String lastName,
            String email
    ) {


        String safeFirstName =
                firstName == null
                        ? ""
                        : firstName.trim();


        String safeLastName =
                lastName == null
                        ? ""
                        : lastName.trim();


        String fullName =
                (
                        safeFirstName
                                +
                                " "
                                +
                                safeLastName
                )
                        .trim();


        if (
                fullName.isBlank()
        ) {

            return email;
        }


        return fullName;
    }


    // =========================================================
    // EXTRACT USER ID
    // =========================================================

    private String extractUserId(
            UserProfileResponse response
    ) {


        if (
                response == null
                        ||
                        response.getUserId() == null
                        ||
                        response
                                .getUserId()
                                .isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "UserService did not return a userId"
            );
        }


        return response
                .getUserId();
    }


    // =========================================================
    // AUTH RESPONSE
    // =========================================================

    private AuthenticationResponse buildAuthenticationResponse(
            User user,
            String token
    ) {


        return AuthenticationResponse
                .builder()

                .token(
                        token
                )

                .id(
                        user.getId()
                )

                .email(
                        user.getEmail()
                )

                .role(
                        user
                                .getRole()
                                .name()
                )

                .userId(
                        user.getUserId()
                )

                .hotelId(
                        user.getHotelId()
                )

                .employeeId(
                        user.getEmployeeId()
                )

                .departmentId(
                        user.getDepartmentId()
                )

                .build();
    }
}