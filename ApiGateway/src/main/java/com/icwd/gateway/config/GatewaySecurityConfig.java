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

    @Value("${jwt.secret}")
    private String jwtSecret;

    /*
     * Validates JWTs created by AuthService.
     * AuthService uses a raw UTF-8 secret, so Gateway
     * must use the same bytes.
     */
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {

        byte[] keyBytes = jwtSecret.getBytes(
                StandardCharsets.UTF_8
        );

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must contain at least 32 bytes"
            );
        }

        SecretKey secretKey = new SecretKeySpec(
                keyBytes,
                "HmacSHA256"
        );

        NimbusReactiveJwtDecoder decoder =
                NimbusReactiveJwtDecoder
                        .withSecretKey(secretKey)
                        .macAlgorithm(MacAlgorithm.HS256)
                        .build();

        decoder.setJwtValidator(
                JwtValidators.createDefault()
        );

        return decoder;
    }

    /*
     * Reads this claim from your AuthService token:
     *
     * "roles": ["ROLE_GUEST"]
     */
    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>>
    jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("roles");

        /*
         * Token already contains ROLE_GUEST,
         * so do not add another prefix.
         */
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );

        return new ReactiveJwtAuthenticationConverterAdapter(
                authenticationConverter
        );
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            Converter<Jwt, Mono<AbstractAuthenticationToken>>
                    jwtConverter
    ) {

        return http

                .csrf(
                        ServerHttpSecurity.CsrfSpec::disable
                )

                /*
                 * Disable Spring's generated login page.
                 */
                .formLogin(
                        ServerHttpSecurity.FormLoginSpec::disable
                )

                /*
                 * Disable username/password HTTP Basic authentication.
                 */
                .httpBasic(
                        ServerHttpSecurity.HttpBasicSpec::disable
                )

                .logout(
                        ServerHttpSecurity.LogoutSpec::disable
                )

                .authorizeExchange(authorize -> authorize

                        /*
                         * Browser preflight requests.
                         */
                        .pathMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()

                        /*
                         * AuthService endpoints must remain public,
                         * otherwise users cannot register or log in.
                         */
                        .pathMatchers(
                                "/api/v1/auth",
                                "/api/v1/auth/**"
                        )
                        .permitAll()

                        /*
                         * Razorpay does not send your application JWT.
                         * This endpoint is protected using
                         * X-Razorpay-Signature.
                         */
                        .pathMatchers(
                                HttpMethod.POST,
                                "/payments/razorpay/webhook"
                        )
                        .permitAll()

                        /*
                         * Public monitoring endpoints.
                         */
                        .pathMatchers(
                                "/actuator/health",
                                "/actuator/info"
                        )
                        .permitAll()

                        /*
                         * Public services.
                         */
                        .pathMatchers(
                                "/hotels",
                                "/hotels/**",
                                "/ratings",
                                "/ratings/**"
                        )
                        .permitAll()

                        /*
                         * Protected services.
                         */
                        .pathMatchers(
                                "/payments",
                                "/payments/**",
                                "/bookings",
                                "/bookings/**",
                                "/users",
                                "/users/**"
                        )
                        .authenticated()

                        /*
                         * Other routes stay public until you
                         * explicitly protect them.
                         */
                        .anyExchange()
                        .permitAll()
                )

                /*
                 * Validate Authorization: Bearer <JWT>.
                 */
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtConverter
                                )
                        )
                )

                .build();
    }
}