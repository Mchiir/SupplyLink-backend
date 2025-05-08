package com.supplylink.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "Welcome to Spring Security";
    }

    @GetMapping("/error")
    public String error_handler() {
        return "An error occurred, this is a fallback message";
    }
}