package ru.effectivemobile.taskmanagement.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.effectivemobile.taskmanagement.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Value("${jwt.secret}")
    private String secret;

    private final long EXPIRATION_MS = 86400000; // One day in milliseconds

    // Generate the secret key from base64 encoded secret
    private SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    /**
     * Generates a JWT token using the user's details.
     *
     * @param userDetails UserDetails object containing user information
     * @return JWT token as a String
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        logger.info("Generating token for user: {}", userDetails.getUsername());

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(jwtSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        logger.info("Generated token for user: {}", userDetails.getUsername());
        return token;
    }

    /**
     * Extracts the username from the token.
     *
     * @param token JWT token as a String
     * @return Username from the token
     */
    @Override
    public String extractUsername(String token) {
        try {
            logger.info("Extracting username from token");
            return Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            logger.error("Failed to extract username from token", e);
            return null; // or handle exception as needed
        }
    }

    /**
     * Validates if the token is valid for the given user.
     *
     * @param token       JWT token as a String
     * @param userDetails UserDetails object containing user information
     * @return True if token is valid, false otherwise
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        boolean valid = username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);

        if (valid) {
            logger.info("Token is valid for user: {}", userDetails.getUsername());
        } else {
            logger.warn("Token is invalid for user: {}", userDetails.getUsername());
        }

        return valid;
    }

    /**
     * Checks if the token is expired.
     *
     * @param token JWT token as a String
     * @return True if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            boolean expired = expiration.before(new Date());

            if (expired) {
                logger.info("Token is expired");
            }

            return expired;
        } catch (JwtException e) {
            logger.error("Failed to parse token for expiration", e);
            return true; // Treat invalid token as expired
        }
    }
}