package com.clevora.clevora.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider - Tạo và xác thực JWT token.
 *
 * Cách sử dụng:
 *   // Tạo token
 *   String token = jwtTokenProvider.generateAccessToken(authentication);
 *
 *   // Validate token
 *   boolean valid = jwtTokenProvider.validateToken(token);
 *
 *   // Lấy email từ token
 *   String email = jwtTokenProvider.getEmailFromToken(token);
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Tạo Access Token từ Authentication object (sau login).
     */
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getUsername(), accessTokenExpirationMs);
    }

    /**
     * Tạo Access Token từ email (dùng cho refresh).
     */
    public String generateAccessToken(String email) {
        return buildToken(email, accessTokenExpirationMs);
    }

    /**
     * Tạo Refresh Token.
     */
    public String generateRefreshToken(String email) {
        return buildToken(email, refreshTokenExpirationMs);
    }

    private String buildToken(String subject, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Lấy email (subject) từ JWT token.
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validate JWT token - kiểm tra chữ ký, hạn sử dụng, format.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
