package com.example.AuthService.config;

import com.example.AuthService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {

        log.info(
                "Initializing UserDetailsService for authentication"
        );

        return username -> {

            log.debug(
                    "Loading user details for authentication. username={}",
                    username
            );

            return userRepository
                    .findByEmail(username)
                    .orElseThrow(() -> {

                        log.warn(
                                "User details could not be loaded because user was not found. username={}",
                                username
                        );

                        return new UsernameNotFoundException(
                                "User not found: " + username
                        );
                    });
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        log.info(
                "Initializing DaoAuthenticationProvider"
        );

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(
                userDetailsService()
        );

        provider.setPasswordEncoder(
                passwordEncoder()
        );

        log.debug(
                "DaoAuthenticationProvider configured successfully"
        );

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        log.info(
                "Initializing AuthenticationManager"
        );

        AuthenticationManager authenticationManager =
                configuration.getAuthenticationManager();

        log.debug(
                "AuthenticationManager initialized successfully"
        );

        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        log.debug(
                "Initializing BCryptPasswordEncoder"
        );

        return new BCryptPasswordEncoder();
    }
}