package com.supplylink.auth;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Dotenv dotenv;
    private final Long jwtExpirationDate;
    private final String jwtSecret;

    public JwtTokenProvider() {
        dotenv = Dotenv.load();
        jwtExpirationDate = Long.parseLong(dotenv.get("JWT_EXPIRATION"));
        jwtSecret = dotenv.get("JWT_SECRET");

        if(jwtSecret.isEmpty() || jwtExpirationDate==null) {
            throw new RuntimeException("JWT_SECRET or JWT_EXPIRATION IS NULL");
        }
    }

    public String generateToken(Authentication authentication) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
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