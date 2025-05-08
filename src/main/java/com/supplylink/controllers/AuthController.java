package com.supplylink.controllers;

import com.supplylink.dtos.*;
import com.supplylink.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Register User
    @PostMapping("/registerUser")
    public ResponseEntity<ApiResponse<UserResDTO>> register(@Valid @RequestBody UserReqDTO userReqDTO) {
        try {
            UserResDTO registeredUser = authService.registerUser(userReqDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", registeredUser));
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Registration failed: " + ex.getMessage()));
        }
    }

    // Login User
    @PostMapping("/loginUser")
    public ResponseEntity<ApiResponse<AuthRes>> login(@RequestBody AuthReq authReq) {
        try {
            String token = authService.loginUser(authReq);

            var authRes = new AuthRes();
            authRes.setAccessToken(token);

            return ResponseEntity.ok(
                    ApiResponse.success("Login successful", authRes)
            );
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Login failed: " + ex.getMessage()));
        }
    }
}