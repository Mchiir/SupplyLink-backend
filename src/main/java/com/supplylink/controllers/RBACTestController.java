package com.supplylink.controllers;

import com.supplylink.dtos.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class RBACTestController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<String>> helloAdmin() {
        return ResponseEntity.ok(ApiResponse.success("Hello Admin", null));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<String>> helloUser() {
        return ResponseEntity.ok(ApiResponse.success("Hello User", null));
    }
}