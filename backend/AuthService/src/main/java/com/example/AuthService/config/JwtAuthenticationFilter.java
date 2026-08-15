package com.example.AuthService.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String path = request.getServletPath();

        boolean shouldSkip =
                path.equals("/api/v1/auth/register")
                        || path.equals("/api/v1/auth/authenticate")
                        || path.equals("/actuator/health")
                        || path.equals("/actuator/info")
                        || path.equals("/error");

        if (shouldSkip) {

            log.trace(
                    "Skipping JWT authentication filter. method={}, path={}",
                    request.getMethod(),
                    path
            );
        }

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path =
                request.getServletPath();

        String authorizationHeader =
                request.getHeader("Authorization");

        log.trace(
                "Processing request through JWT authentication filter. method={}, path={}",
                request.getMethod(),
                path
        );

        /*
         * Missing token is not automatically an error.
         * The SecurityFilterChain decides whether the URL
         * requires authentication.
         */
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            log.trace(
                    "No Bearer token found. Continuing request without JWT authentication. method={}, path={}",
                    request.getMethod(),
                    path
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                authorizationHeader
                        .substring(7)
                        .trim();

        if (token.isBlank()) {

            log.warn(
                    "JWT authentication rejected because Bearer token is empty. method={}, path={}",
                    request.getMethod(),
                    path
            );

            writeUnauthorized(
                    response,
                    "JWT token is empty"
            );

            return;
        }

        try {

            log.trace(
                    "Extracting username from JWT. path={}",
                    path
            );

            String username =
                    jwtService.extractUsername(
                            token
                    );

            log.debug(
                    "Username extracted from JWT successfully. username={}, path={}",
                    username,
                    path
            );

            if (username != null
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                log.debug(
                        "Loading user details for JWT authentication. username={}",
                        username
                );

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(
                                        username
                                );

                log.trace(
                        "Validating JWT for user. username={}",
                        username
                );

                if (!jwtService.isTokenValid(
                        token,
                        userDetails
                )) {

                    log.warn(
                            "JWT validation failed. username={}, method={}, path={}",
                            username,
                            request.getMethod(),
                            path
                    );

                    writeUnauthorized(
                            response,
                            "Invalid or expired JWT token"
                    );

                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(
                                        request
                                )
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );

                log.debug(
                        "JWT authentication successful. username={}, method={}, path={}",
                        username,
                        request.getMethod(),
                        path
                );

            } else if (username == null) {

                log.warn(
                        "JWT did not contain a valid username. method={}, path={}",
                        request.getMethod(),
                        path
                );

            } else {

                log.trace(
                        "Security context already contains authentication. Skipping JWT authentication. path={}",
                        path
                );
            }

            filterChain.doFilter(
                    request,
                    response
            );

        } catch (JwtException |
                 IllegalArgumentException exception) {

            SecurityContextHolder
                    .clearContext();

            log.warn(
                    "JWT processing failed. method={}, path={}, reason={}",
                    request.getMethod(),
                    path,
                    exception.getMessage()
            );

            writeUnauthorized(
                    response,
                    "Invalid or expired JWT token"
            );
        }
    }

    private void writeUnauthorized(
            HttpServletResponse response,
            String message
    ) throws IOException {

        log.debug(
                "Writing HTTP 401 Unauthorized response. message={}",
                message
        );

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.getWriter().write(
                """
                {
                  "status": 401,
                  "error": "Unauthorized",
                  "message": "%s"
                }
                """.formatted(message)
        );
    }
}