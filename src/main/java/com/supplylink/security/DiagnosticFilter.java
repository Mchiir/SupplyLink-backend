package com.supplylink.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DiagnosticFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();
        logger.info("Origin header: {}", request.getHeader("Origin"));
        logger.info("Request received: {} {}", method, path);
        logger.info("Headers:");
        request.getHeaderNames().asIterator().forEachRemaining(headerName ->
                logger.info("  {}: {}", headerName, request.getHeader(headerName))
        );

        filterChain.doFilter(request, response);

        logger.info("Response status: {}", response.getStatus());
    }
}