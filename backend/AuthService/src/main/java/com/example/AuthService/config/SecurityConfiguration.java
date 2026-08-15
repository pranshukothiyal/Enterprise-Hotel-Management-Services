package com.example.AuthService.config;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {


    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;


    private final AuthenticationProvider
            authenticationProvider;


    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {


        log.info(
                "Configuring AuthService security filter chain"
        );


        http


                // =================================================
                // CSRF
                // =================================================

                .csrf(
                        AbstractHttpConfigurer::disable
                )


                // =================================================
                // FORM LOGIN
                // =================================================

                .formLogin(
                        AbstractHttpConfigurer::disable
                )


                // =================================================
                // BASIC AUTH
                // =================================================

                .httpBasic(
                        AbstractHttpConfigurer::disable
                )


                // =================================================
                // STATELESS SESSION
                // =================================================

                .sessionManagement(
                        session ->

                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )


                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(
                        authorize ->

                                authorize


                                        // =================================
                                        // REGISTER
                                        // PUBLIC
                                        // =================================

                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/api/v1/auth/register"
                                        )
                                        .permitAll()


                                        // =================================
                                        // LOGIN
                                        // PUBLIC
                                        // =================================

                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/api/v1/auth/authenticate"
                                        )
                                        .permitAll()


                                        // =================================
                                        // AUTH VALIDATION
                                        // =================================

                                        .requestMatchers(
                                                "/api/v1/auth/validate"
                                        )
                                        .authenticated()


                                        // =================================
                                        // ACCOUNT MANAGEMENT
                                        // ADMIN ONLY
                                        // =================================
                                        //
                                        // Examples:
                                        //
                                        // GET
                                        // /api/v1/accounts
                                        //
                                        // GET
                                        // /api/v1/accounts/managers
                                        //
                                        // PATCH
                                        // /api/v1/accounts/{id}/hotel
                                        //
                                        // DELETE
                                        // /api/v1/accounts/{id}/hotel
                                        //
                                        // =================================

                                        .requestMatchers(
                                                "/api/v1/accounts",
                                                "/api/v1/accounts/**"
                                        )
                                        .hasRole(
                                                "ADMIN"
                                        )


                                        // =================================
                                        // ACTUATOR
                                        // =================================

                                        .requestMatchers(
                                                "/actuator/health",
                                                "/actuator/info"
                                        )
                                        .permitAll()


                                        // =================================
                                        // ERROR
                                        // =================================

                                        .requestMatchers(
                                                "/error"
                                        )
                                        .permitAll()


                                        // =================================
                                        // EVERYTHING ELSE
                                        // =================================

                                        .anyRequest()
                                        .authenticated()
                )


                // =================================================
                // AUTHENTICATION PROVIDER
                // =================================================

                .authenticationProvider(
                        authenticationProvider
                )


                // =================================================
                // JWT FILTER
                // =================================================

                .addFilterBefore(

                        jwtAuthenticationFilter,

                        UsernamePasswordAuthenticationFilter.class
                )


                // =================================================
                // EXCEPTION HANDLING
                // =================================================

                .exceptionHandling(
                        exception ->

                                exception


                                        // =================================
                                        // 401
                                        // =================================

                                        .authenticationEntryPoint(
                                                (
                                                        request,
                                                        response,
                                                        authException
                                                ) -> {


                                                    log.warn(
                                                            "Unauthenticated request rejected. method={}, path={}",
                                                            request.getMethod(),
                                                            request.getServletPath()
                                                    );


                                                    response.setStatus(
                                                            HttpServletResponse.SC_UNAUTHORIZED
                                                    );


                                                    response.setContentType(
                                                            MediaType.APPLICATION_JSON_VALUE
                                                    );


                                                    response
                                                            .getWriter()
                                                            .write(
                                                                    """
                                                                    {
                                                                      "status": 401,
                                                                      "error": "Unauthorized",
                                                                      "message": "Authentication is required"
                                                                    }
                                                                    """
                                                            );
                                                }
                                        )


                                        // =================================
                                        // 403
                                        // =================================

                                        .accessDeniedHandler(
                                                (
                                                        request,
                                                        response,
                                                        accessDeniedException
                                                ) -> {


                                                    log.warn(
                                                            "Forbidden request rejected. method={}, path={}",
                                                            request.getMethod(),
                                                            request.getServletPath()
                                                    );


                                                    response.setStatus(
                                                            HttpServletResponse.SC_FORBIDDEN
                                                    );


                                                    response.setContentType(
                                                            MediaType.APPLICATION_JSON_VALUE
                                                    );


                                                    response
                                                            .getWriter()
                                                            .write(
                                                                    """
                                                                    {
                                                                      "status": 403,
                                                                      "error": "Forbidden",
                                                                      "message": "You do not have permission to perform this action"
                                                                    }
                                                                    """
                                                            );
                                                }
                                        )
                );


        SecurityFilterChain securityFilterChain =
                http.build();


        log.info(
                "AuthService security filter chain configured successfully. sessionPolicy=STATELESS, jwtFilterEnabled=true"
        );


        return securityFilterChain;
    }
}