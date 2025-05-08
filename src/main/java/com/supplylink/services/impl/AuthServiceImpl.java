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

        String identifier = buildIdentifier(authReq.getEmail(), authReq.getPhoneNumber());
        String password = authReq.getPassword();

        String resolvedIdentifier = resolveIdentifier(identifier);

        System.out.println("Login identifier: "+ resolvedIdentifier);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            resolvedIdentifier,
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

    private String buildIdentifier(String email, String phone) {
        return (email == null ? "" : email) + ":" + (phone == null ? "" : phone);
    }
    private String resolveIdentifier(String identifier) {
        String[] parts = identifier.split(":");
        String email = parts.length > 0 ? parts[0] : null;
        String phone = parts.length > 1 ? parts[1] : null;

        User user = null;

        if (email != null && !email.isBlank()) {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new InvalidRequestException("User not found by email"));
        }
        if (user == null && phone != null && !phone.isBlank()) {
            user = userRepository.findByPhoneNumber(phone)
                    .orElseThrow(() -> new InvalidRequestException("User not found by phone number"));
        }

        if (user == null) {
            throw new InvalidRequestException("No user found with provided email or phone");
        }

        // Return the identifier that matched
        return email != null && user.getEmail().equals(email) ? email : phone;
    }
}