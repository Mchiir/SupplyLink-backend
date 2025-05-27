package com.supplylink.auth;

import com.supplylink.repositories.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final Dotenv dotenv;
    private final Long jwtExpirationDate;
    private final String jwtSecret;
    private final UserRepository userRepository;

    public JwtTokenProvider(UserRepository userRepository) {
        dotenv = Dotenv.load();
        jwtExpirationDate = Long.parseLong(dotenv.get("JWT_EXPIRATION"));
        jwtSecret = dotenv.get("JWT_SECRET");

        if(jwtSecret.isEmpty() || jwtExpirationDate==null) {
            throw new RuntimeException("JWT_SECRET or JWT_EXPIRATION IS NULL");
        }

        this.userRepository = userRepository;
    }

    public String generateToken(Authentication authentication) {
        try {
            Date currentDate = new Date();
            Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

            String loginIdentifier = authentication.getName();
            String email = "", phoneNumber = "", userId = "";

            if (loginIdentifier.contains(":")) {
                String[] parts = loginIdentifier.split(":", -1); // keep empty strings
                email = parts[0].trim();
                phoneNumber = parts.length > 1 ? parts[1].trim() : "";
            }

            JwtBuilder builder = Jwts.builder()
                    .setIssuedAt(currentDate)
                    .setExpiration(expireDate)
                    .signWith(key(), SignatureAlgorithm.HS256);

            // Determine which claims to include
            if (!email.isEmpty() && !phoneNumber.isEmpty()) {
                userId = userRepository.findByEmailAndPhoneNumber(email, phoneNumber)
                        .orElseThrow(() -> new RuntimeException("User not found with that email and phone"))
                        .getId().toString();

                builder.setSubject(userId)
                        .claim("email", email)
                        .claim("phoneNumber", phoneNumber);

            } else if (!email.isEmpty()) {
                userId = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found with that email"))
                        .getId().toString();

                builder.setSubject(userId)
                        .claim("email", email);

            } else if (!phoneNumber.isEmpty()) {
                userId = userRepository.findByPhoneNumber(phoneNumber)
                        .orElseThrow(() -> new RuntimeException("User not found with that phone"))
                        .getId().toString();

                builder.setSubject(userId)
                        .claim("phoneNumber", phoneNumber);
            } else {
                throw new IllegalStateException("Neither email nor phone number provided in loginIdentifier: " + loginIdentifier);
            }

            return builder.compact();

        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token: " + e.getMessage(), e);
        }
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // extract username from JWT token
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(key())
                .parseClaimsJws(token)
                .getBody();
    }

    // validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key()).
                    parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Error with token validation: " + e.getMessage());
            return false;
        }
    }
}