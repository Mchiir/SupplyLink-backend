package com.supplylink.auth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private UserDetailsService userDetailsService;

    //Constructor
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }


    // This method is executed for every request intercepted by the filter.
    //And, it extract the token from the request header and validate the token.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Skip filtering for actuator and auth endpoints
        if (
                request.getRequestURI().equals("/v3/api-docs") ||
                request.getRequestURI().equals("/error") ||
                request.getRequestURI().startsWith("/v3/api-docs") ||
                request.getRequestURI().startsWith("/swagger-ui") ||
                request.getRequestURI().startsWith("/api/auth") ||
                request.getRequestURI().startsWith("/manage/health") ||
                request.getRequestURI().startsWith("/manage/info")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        // Validate Token
        if(StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)){
            // get email from token
            Claims claims = jwtTokenProvider.getAllClaimsFromToken(token);
//            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            String phoneNumber = claims.get("phoneNumber", String.class);
            String userIdentifier = buildIdentifier(email, phoneNumber);

            UserDetails userDetails = userDetailsService.loadUserByUsername(userIdentifier);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    // Extract the token
    private String getTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7, bearerToken.length());
        }

        return null;
    }

    public String buildIdentifier(String email, String phone) {
        email = (email != null) ? email.trim() : "";
        phone = (phone != null) ? phone.trim() : "";

        if (!email.isEmpty() && !phone.isEmpty()) return email + ":" + phone;  // both
        if (!email.isEmpty()) return email + ":";          // only email
        if (!phone.isEmpty()) return ":" + phone;          // only phone

        throw new IllegalArgumentException("Neither of email or password passed");                  // neither
    }
}