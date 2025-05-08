package com.supplylink.controllers;

import com.supplylink.dtos.AuthReq;
import com.supplylink.dtos.AuthRes;
import com.supplylink.dtos.UserReqDTO;
import com.supplylink.dtos.UserResDTO;
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
    public ResponseEntity<?> register(@Valid @RequestBody UserReqDTO userReqDTO) {
        try {
            UserResDTO registeredUser = authService.registerUser(userReqDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    // login User
    @PostMapping("/loginUser")
    public ResponseEntity<AuthRes> login(@RequestBody AuthReq authReq) {

        String token = authService.loginUser(authReq);

        var authRes = new AuthRes();
        authRes.setAccessToken(token);

        //03 - Return the response to the user
        return new ResponseEntity<>(authRes, HttpStatus.OK);
    }
}