package com.supplylink.services.impl;

import com.supplylink.auth.JwtTokenProvider;
import com.supplylink.dtos.*;
import com.supplylink.exceptions.InvalidRequestException;
import com.supplylink.models.Location;
import com.supplylink.models.Role;
import com.supplylink.models.User;
import com.supplylink.repositories.UserRepository;
import com.supplylink.services.AuthService;
import com.supplylink.validations.AuthReqValidator;
import com.supplylink.validations.UserReqDTOValidator;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthReqValidator authReqValidator;
    private final UserReqDTOValidator userReqDTOValidator;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           AuthReqValidator authReqValidator,
                           UserReqDTOValidator userReqDTOValidator) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authReqValidator = authReqValidator;
        this.userReqDTOValidator = userReqDTOValidator;
    }

    @Override
    public String loginUser(AuthReq authReq) {
        // Validate request first
        authReqValidator.validate(authReq);

        String loginIdentifier = buildIdentifier(authReq.getEmail(), authReq.getPhoneNumber());

        System.out.println("Login identifier: "+ loginIdentifier);

        if(!validateLoginIdentifier(loginIdentifier))
            throw new InvalidRequestException("Invalid email or phone number");


        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginIdentifier,
                            authReq.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            return token;
        } catch (Exception ex) {
            throw new InvalidRequestException("Invalid login credentials");
        }
    }

    private String buildIdentifier(String email, String phone) {
        email = (email != null) ? email.trim() : "";
        phone = (phone != null) ? phone.trim() : "";

        if (!email.isEmpty() && !phone.isEmpty()) {
            return email + ":" + phone;  // both
        } else if (!email.isEmpty()) {
            return email + ":";          // only email
        } else if (!phone.isEmpty()) {
            return ":" + phone;          // only phone
        } else {
            return "";                   // neither
        }
    }


    private boolean validateLoginIdentifier(String loginIdentifier) {
        String email = "", phoneNumber = "";

        if (loginIdentifier.contains(":")) {
            String[] parts = loginIdentifier.split(":", -1); // preserve empty trailing values

            if (parts.length == 2) {
                email = parts[0].trim();
                phoneNumber = parts[1].trim();
            } else {
                throw new InvalidRequestException("Malformed login identifier: " + loginIdentifier);
            }
        }

        if (!email.isEmpty() && !phoneNumber.isEmpty()) {
            return userRepository.existsByEmailAndPhoneNumber(email, phoneNumber);
        } else if (!email.isEmpty()) {
            return userRepository.existsByEmail(email);
        } else if (!phoneNumber.isEmpty()) {
            return userRepository.existsByPhoneNumber(phoneNumber);
        }

        return false;
    }

    @Override
    public UserResDTO registerUser(UserReqDTO userReqDTO) {
        // Validate registration data
        userReqDTOValidator.validate(userReqDTO);

        // Check for existing users
        if (userReqDTO.getEmail() != null &&
                userRepository.existsByEmail(userReqDTO.getEmail())) {
            throw new InvalidRequestException("Email already in use");
        }

        if (userReqDTO.getPhoneNumber() != null &&
                userRepository.existsByPhoneNumber(userReqDTO.getPhoneNumber())) {
            throw new InvalidRequestException("Phone number already in use");
        }

        LocationDTO locationDTO = userReqDTO.getLocationDTO();
        Location location = new Location(locationDTO.getDistrict(), locationDTO.getProvince(), locationDTO.getCountry());

        // Create new user
        User newUser = modelMapper.map(userReqDTO, User.class);
        newUser.setLocation(location);
        newUser.setRoles(Set.of(new Role("ROLE_USER"))); // Default role
        newUser.setPassword(passwordEncoder.encode(userReqDTO.getPassword()));

        try {
            User savedUser = userRepository.save(newUser);
            return modelMapper.map(savedUser, UserResDTO.class);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException("Registration failed: " +
                    ex.getMostSpecificCause().getMessage());
        }
    }
}