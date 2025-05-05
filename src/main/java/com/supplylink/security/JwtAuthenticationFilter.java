package com.supplylink.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/company/login",
            "/api/auth/admin/login",
            "/api/companies/login",
            "/api/auth/reset-password",
            "/api/test/public",
            "/error"
    );

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil,
            @Qualifier("userService") UserDetailsService userDetailsService,
            BusCompanyService busCompanyService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.busCompanyService = busCompanyService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        boolean shouldSkip = PUBLIC_ENDPOINTS.contains(path) || request.getMethod().equals("OPTIONS");
        if (shouldSkip) {
            logger.debug("Skipping JWT authentication for: " + path + " Method: " + request.getMethod());
        }
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        logger.debug("Processing request: " + path);

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;
        String role = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractUsername(jwt);
                role = jwtUtil.extractRole(jwt);
                logger.debug("Email: " + email + ", Role: " + role);
            } catch (Exception e) {
                logger.error("Error extracting information from token: " + e.getMessage());
            }
        } else {
            logger.debug("No Authorization header or invalid format for path: " + path);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails;

                // Choose the appropriate service based on the role in the token
                if (role != null && role.equals("COMPANY")) {
                    logger.debug("Loading company details for: " + email);
                    userDetails = busCompanyService.loadUserByUsername(email);
                } else {
                    logger.debug("Loading user details for: " + email);
                    userDetails = userDetailsService.loadUserByUsername(email);
                }

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.debug("Authenticated entity: " + email + " with role: " + role + " for path: " + path);
                } else {
                    logger.warn("Invalid JWT token for: " + email);
                }
            } catch (Exception e) {
                logger.error("Error authenticating: " + e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}