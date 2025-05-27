package com.supplylink.context;

import com.supplylink.auth.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
public class ContextAccessor {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Extracts userId (UUID) from JWT token subject
    public UUID getCurrentUserId(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Claims claims = jwtTokenProvider.getAllClaimsFromToken(token);
        String subject = claims.getSubject(); // typically your userId
        try {
            return UUID.fromString(subject);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid userId in token subject");
        }
    }

    // Helper to extract bearer token from Authorization header
    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalArgumentException("Authorization token is missing or malformed");
    }
}
