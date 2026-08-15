package com.icwd.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;

import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;


@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {


    // =========================================================
    // JWT SECRET
    // =========================================================

    @Value("${jwt.secret}")
    private String jwtSecret;


    // =========================================================
    // JWT DECODER
    // =========================================================

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {


        byte[] keyBytes =
                jwtSecret.getBytes(
                        StandardCharsets.UTF_8
                );


        if (keyBytes.length < 32) {

            throw new IllegalStateException(
                    "JWT secret must contain at least 32 bytes"
            );
        }


        SecretKey secretKey =
                new SecretKeySpec(
                        keyBytes,
                        "HmacSHA256"
                );


        NimbusReactiveJwtDecoder decoder =
                NimbusReactiveJwtDecoder
                        .withSecretKey(
                                secretKey
                        )
                        .macAlgorithm(
                                MacAlgorithm.HS256
                        )
                        .build();


        decoder.setJwtValidator(
                JwtValidators.createDefault()
        );


        return decoder;
    }


    // =========================================================
    // JWT ROLE CONVERTER
    // =========================================================
    //
    // Expected token:
    //
    // "roles": ["ROLE_ADMIN"]
    // "roles": ["ROLE_HOTEL_MANAGER"]
    // "roles": ["ROLE_EMPLOYEE"]
    // "roles": ["ROLE_GUEST"]
    //
    // =========================================================

    @Bean
    public Converter<
            Jwt,
            Mono<AbstractAuthenticationToken>
            > jwtAuthenticationConverter() {


        JwtGrantedAuthoritiesConverter
                authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();


        authoritiesConverter
                .setAuthoritiesClaimName(
                        "roles"
                );


        /*
         * JWT already contains ROLE_.
         *
         * So:
         *
         * ROLE_ADMIN
         * ROLE_GUEST
         *
         * must NOT become:
         *
         * ROLE_ROLE_ADMIN
         */
        authoritiesConverter
                .setAuthorityPrefix("");


        JwtAuthenticationConverter
                authenticationConverter =
                new JwtAuthenticationConverter();


        authenticationConverter
                .setJwtGrantedAuthoritiesConverter(
                        authoritiesConverter
                );


        return new ReactiveJwtAuthenticationConverterAdapter(
                authenticationConverter
        );
    }


    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,

            Converter<
                    Jwt,
                    Mono<AbstractAuthenticationToken>
                    > jwtConverter
    ) {


        return http


                // =================================================
                // CSRF
                // =================================================

                .csrf(
                        ServerHttpSecurity.CsrfSpec::disable
                )


                // =================================================
                // FORM LOGIN
                // =================================================

                .formLogin(
                        ServerHttpSecurity
                                .FormLoginSpec::disable
                )


                // =================================================
                // HTTP BASIC
                // =================================================

                .httpBasic(
                        ServerHttpSecurity
                                .HttpBasicSpec::disable
                )


                // =================================================
                // LOGOUT
                // =================================================

                .logout(
                        ServerHttpSecurity
                                .LogoutSpec::disable
                )


                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeExchange(
                        authorize ->
                                authorize


                                        // =================================
                                        // OPTIONS / CORS PREFLIGHT
                                        // =================================

                                        .pathMatchers(
                                                HttpMethod.OPTIONS,
                                                "/**"
                                        )
                                        .permitAll()


                                        // =================================
                                        // AUTH SERVICE
                                        // PUBLIC
                                        // =================================

                                        .pathMatchers(
                                                "/api/v1/auth",
                                                "/api/v1/auth/**"
                                        )
                                        .permitAll()


                                        // =================================
                                        // RAZORPAY WEBHOOK
                                        // =================================

                                        .pathMatchers(
                                                HttpMethod.POST,
                                                "/payments/razorpay/webhook"
                                        )
                                        .permitAll()


                                        // =================================
                                        // ACTUATOR
                                        // =================================

                                        .pathMatchers(
                                                "/actuator/health",
                                                "/actuator/info"
                                        )
                                        .permitAll()


                                        // =================================
                                        // PUBLIC HOTEL INFORMATION
                                        // =================================
                                        //
                                        // Anyone can browse:
                                        //
                                        // Hotels
                                        // Rooms
                                        // Ratings
                                        //
                                        // =================================

                                        .pathMatchers(
                                                HttpMethod.GET,

                                                "/hotels",
                                                "/hotels/**",

                                                "/rooms",
                                                "/rooms/**",

                                                "/ratings",
                                                "/ratings/**"
                                        )
                                        .permitAll()


                                        // =================================
                                        // USERS
                                        // ADMIN ONLY
                                        // =================================
                                        //
                                        // IMPORTANT:
                                        //
                                        // Guest must NOT get:
                                        //
                                        // GET /users
                                        //
                                        // We use userId from AuthService
                                        // instead.
                                        //
                                        // =================================

                                        .pathMatchers(
                                                "/users",
                                                "/users/**"
                                        )
                                        .hasRole(
                                                "ADMIN"
                                        )


                                        // =================================
                                        // HOTEL / ROOM MANAGEMENT
                                        // =================================

                                        .pathMatchers(
                                                "/hotels",
                                                "/hotels/**",

                                                "/rooms",
                                                "/rooms/**"
                                        )
                                        .hasAnyRole(
                                                "ADMIN",
                                                "HOTEL_MANAGER"
                                        )


                                        // =================================
                                        // EMPLOYEES / DEPARTMENTS
                                        // =================================

                                        .pathMatchers(
                                                "/employees",
                                                "/employees/**",

                                                "/departments",
                                                "/departments/**"
                                        )
                                        .hasAnyRole(
                                                "ADMIN",
                                                "HOTEL_MANAGER"
                                        )


                                        // =================================
                                        // HOTEL SERVICE CATALOGUE
                                        // =================================

                                        .pathMatchers(
                                                "/hotelservices",
                                                "/hotelservices/**"
                                        )
                                        .hasAnyRole(
                                                "ADMIN",
                                                "HOTEL_MANAGER"
                                        )


                                        // =================================
                                        // BOOKINGS
                                        // =================================

                                        .pathMatchers(
                                                "/bookings",
                                                "/bookings/**"
                                        )
                                        .hasAnyRole(
                                                "ADMIN",
                                                "HOTEL_MANAGER",
                                                "EMPLOYEE",
                                                "GUEST"
                                        )


                                        // =================================
                                        // PAYMENTS
                                        // =================================

                                        .pathMatchers(
                                                "/payments",
                                                "/payments/**"
                                        )
                                        .hasAnyRole(
                                                "ADMIN",
                                                "HOTEL_MANAGER",
                                                "GUEST"
                                        )


                                        // =================================
                                        // INVOICES
                                        // =================================
                                        //
                                        // IMPORTANT CHANGE:
                                        //
                                        // GUEST is now allowed through
                                        // Gateway.
                                        //
                                        // React sends:
                                        //
                                        // /invoices?userId=<guest-userId>
                                        //
                                        // =================================

                                        .pathMatchers(
                                                "/invoices",
                                                "/invoices/**"
                                        )
                                        .hasAnyRole(
                                                "ADMIN",
                                                "HOTEL_MANAGER",
                                                "GUEST"
                                        )


                                        // =================================
                                        // ROOM SERVICE REQUESTS
                                        // =================================

                                        .pathMatchers(
                                                "/roomservicerequests",
                                                "/roomservicerequests/**"
                                        )
                                        .hasAnyRole(
                                                "ADMIN",
                                                "HOTEL_MANAGER",
                                                "EMPLOYEE",
                                                "GUEST"
                                        )


                                        // =================================
                                        // RATINGS
                                        // =================================
                                        //
                                        // GET already matched public rule.
                                        //
                                        // This rule handles modification.
                                        //
                                        // =================================

                                        .pathMatchers(
                                                "/ratings",
                                                "/ratings/**"
                                        )
                                        .hasAnyRole(
                                                "ADMIN",
                                                "HOTEL_MANAGER",
                                                "GUEST"
                                        )


                                        // =================================
                                        // NOTIFICATIONS
                                        // =================================

                                        .pathMatchers(
                                                "/notifications",
                                                "/notifications/**"
                                        )
                                        .authenticated()


                                        // =================================
                                        // AI ASSISTANT
                                        // =================================

                                        .pathMatchers(
                                                "/api/ai",
                                                "/api/ai/**"
                                        )
                                        .authenticated()


                                        // =================================
                                        // EVERYTHING ELSE
                                        // =================================

                                        .anyExchange()
                                        .authenticated()
                )


                // =================================================
                // JWT RESOURCE SERVER
                // =================================================

                .oauth2ResourceServer(
                        oauth2 ->

                                oauth2.jwt(
                                        jwt ->

                                                jwt.jwtAuthenticationConverter(
                                                        jwtConverter
                                                )
                                )
                )


                .build();
    }
}