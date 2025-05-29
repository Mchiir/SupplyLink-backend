package com.supplylink.controllers;

import com.supplylink.dtos.res.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> home() {
        return ResponseEntity.ok(
                ApiResponse.success("Welcome to SupplyLink backend", null)
        );
    }

    @GetMapping("/error")
    public ResponseEntity<ApiResponse<String>> errorHandler() {
        return ResponseEntity.ok(ApiResponse.error("An error occurred, this is a fallback message"));
    }
}
