package com.supplylink.controllers;

import com.supplylink.dtos.req.TwilioRequest;
import com.supplylink.dtos.req.UserLoginReqDTO;
import com.supplylink.dtos.req.UserRegistrationReqDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.res.UserLoginResDTO;
import com.supplylink.dtos.res.UserRegistrationResDTO;
import com.supplylink.exceptions.InvalidRequestException;
import com.supplylink.models.User;
import com.supplylink.models.VerificationToken;
import com.supplylink.repositories.UserRepository;
import com.supplylink.repositories.VerificationTokenRepository;
import com.supplylink.services.AuthService;
import com.supplylink.services.SmsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SmsService smsService;

    // Register User
    @PostMapping("/registerUser")
    public ResponseEntity<ApiResponse<UserRegistrationResDTO>> register(
            @Valid @RequestBody UserRegistrationReqDTO userRegistrationReqDTO) {
        try {
            UserRegistrationResDTO registeredUser = authService.registerUser(userRegistrationReqDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", registeredUser));
        } catch (InvalidRequestException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    // Verify email token
    @GetMapping("/verifyEmail")
    public ResponseEntity<?> verifyAccount(@RequestParam String token) {
        try {
            VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
            if (verificationToken == null || verificationToken.getExpiryDate().before(new Date())) {
                return ResponseEntity.badRequest().body("Invalid or expired token.");
            }

            User user = verificationToken.getUser();
            user.setVerified(true);
            userRepository.save(user);

            verificationTokenRepository.delete(verificationToken);

            return ResponseEntity.ok("Email verified successfully!");
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during verification: " + e.getMessage());
        }
    }

    // Login User with isVerified check
    @PostMapping("/loginUser")
    public ResponseEntity<ApiResponse<UserLoginResDTO>> login(@Valid @RequestBody UserLoginReqDTO authReq) {
        try {
            String token = authService.loginUser(authReq);

            var authRes = new UserLoginResDTO();
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

    @PostMapping("/verifyPhone")
    @PreAuthorize("hasRole('SYSTEM')")
    public ResponseEntity<String> verifyPhone(@Valid @RequestBody TwilioRequest request) {
        try{
            smsService.sendSms(request);
            return ResponseEntity.ok("Sms sent successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}