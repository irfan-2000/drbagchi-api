package com.drbagchisclasses.drbagchi_api.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY_STRING =
            "mysupersecurekeyforjwttokengeneration1234567890"; // 40+ chars

    private static final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 hours

    /**
     * Generate JWT token for given username & userId.
     */
    public static String generateToken(String userName, String userId,String Email)
    {
        return Jwts.builder()
                .setSubject(userName)
                .claim("UserId", userId).claim("Email",Email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate token signature & expiration.
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException ex) {
            ExceptionLogger.logException(ex);
            return false;
        }
    }

    /**
     * Extract username (subject) from token.
     */
    public static String extractUsername(String token)
    {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException ex) {
            ExceptionLogger.logException(ex);
            return null;
        }
    }

    /**
     * Extract UserId claim from token.
     */
    public static String extractUserId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("UserId", String.class);
        } catch (JwtException ex) {
            ExceptionLogger.logException(ex);
            return null;
        }
    }


    public static String extractEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("Email", String.class);
        } catch (JwtException ex) {
            ExceptionLogger.logException(ex);
            return null;
        }
    }




}
