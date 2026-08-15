package com.example.AuthService.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    public String extractUsername(String token) {

        log.trace(
                "Extracting username from JWT"
        );

        String username = extractClaim(
                token,
                Claims::getSubject
        );

        log.debug(
                "Username extracted from JWT successfully. username={}",
                username
        );

        return username;
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {

        log.trace(
                "Extracting roles from JWT"
        );

        List<String> roles = extractClaim(
                token,
                claims -> claims.get(
                        "roles",
                        List.class
                )
        );

        log.debug(
                "Roles extracted from JWT successfully. roleCount={}",
                roles != null ? roles.size() : 0
        );

        return roles;
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        log.trace(
                "Extracting claim from JWT"
        );

        Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(
                claims
        );
    }

    public String generateToken(
            UserDetails userDetails
    ) {

        log.info(
                "Starting JWT generation. username={}",
                userDetails.getUsername()
        );

        Map<String, Object> claims =
                new HashMap<>();

        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(
                        GrantedAuthority::getAuthority
                )
                .toList();

        claims.put(
                "roles",
                roles
        );

        log.debug(
                "JWT claims prepared. username={}, roles={}",
                userDetails.getUsername(),
                roles
        );

        String token = generateToken(
                claims,
                userDetails
        );

        log.info(
                "JWT generated successfully. username={}",
                userDetails.getUsername()
        );

        return token;
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {

        Date issuedAt =
                new Date();

        Date expiration =
                new Date(
                        issuedAt.getTime()
                                + jwtExpirationMs
                );

        log.debug(
                "Building JWT. username={}, issuedAt={}, expiresAt={}",
                userDetails.getUsername(),
                issuedAt,
                expiration
        );

        String token =
                Jwts.builder()
                        .setClaims(
                                extraClaims
                        )
                        .setSubject(
                                userDetails.getUsername()
                        )
                        .setIssuedAt(
                                issuedAt
                        )
                        .setExpiration(
                                expiration
                        )
                        .signWith(
                                getSigningKey(),
                                SignatureAlgorithm.HS256
                        )
                        .compact();

        log.debug(
                "JWT build completed successfully. username={}",
                userDetails.getUsername()
        );

        return token;
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        log.trace(
                "Validating JWT. username={}",
                userDetails.getUsername()
        );

        try {

            String username =
                    extractUsername(
                            token
                    );

            boolean valid =
                    username != null
                            && username.equals(
                            userDetails.getUsername()
                    )
                            && !isTokenExpired(
                            token
                    );

            if (valid) {

                log.debug(
                        "JWT validation successful. username={}",
                        userDetails.getUsername()
                );

            } else {

                log.warn(
                        "JWT validation failed. username={}",
                        userDetails.getUsername()
                );
            }

            return valid;

        } catch (JwtException |
                 IllegalArgumentException exception) {

            log.warn(
                    "JWT validation failed due to invalid token. username={}, reason={}",
                    userDetails.getUsername(),
                    exception.getMessage()
            );

            return false;
        }
    }

    private boolean isTokenExpired(
            String token
    ) {

        Date expiration =
                extractExpiration(
                        token
                );

        boolean expired =
                expiration.before(
                        new Date()
                );

        log.trace(
                "JWT expiration checked. expired={}, expiration={}",
                expired,
                expiration
        );

        return expired;
    }

    private Date extractExpiration(
            String token
    ) {

        log.trace(
                "Extracting JWT expiration"
        );

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    private Claims extractAllClaims(
            String token
    ) {

        log.trace(
                "Parsing JWT claims"
        );

        Claims claims =
                Jwts.parserBuilder()
                        .setSigningKey(
                                getSigningKey()
                        )
                        .build()
                        .parseClaimsJws(
                                token
                        )
                        .getBody();

        log.trace(
                "JWT claims parsed successfully"
        );

        return claims;
    }

    private Key getSigningKey() {

        byte[] keyBytes =
                secretKey.getBytes(
                        StandardCharsets.UTF_8
                );

        if (keyBytes.length < 32) {

            log.error(
                    "JWT signing key configuration is invalid. keyLength={} bytes, requiredMinimum=32 bytes",
                    keyBytes.length
            );

            throw new IllegalStateException(
                    "JWT secret must contain at least 32 characters"
            );
        }

        log.trace(
                "JWT signing key created successfully"
        );

        return Keys.hmacShaKeyFor(
                keyBytes
        );
    }
}