package com.supplylink.services.impl;

import com.supplylink.auth.JwtTokenProvider;
import com.supplylink.dtos.req.UserLoginReqDTO;
import com.supplylink.dtos.LocationDTO;
import com.supplylink.dtos.req.UserRegistrationReqDTO;
import com.supplylink.exceptions.InvalidRequestException;
import com.supplylink.models.Location;
import com.supplylink.models.Role;
import com.supplylink.models.User;
import com.supplylink.models.VerificationToken;
import com.supplylink.repositories.UserRepository;
import com.supplylink.repositories.VerificationTokenRepository;
import com.supplylink.services.AuthService;
import com.supplylink.services.EmailService;
import com.supplylink.validations.AuthReqValidator;
import com.supplylink.validations.UserReqDTOValidator;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    private final AuthReqValidator authReqValidator;
    private final UserReqDTOValidator userReqDTOValidator;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           @Qualifier("smtpEmailSender") EmailService emailService,
                           AuthReqValidator authReqValidator,
                           UserReqDTOValidator userReqDTOValidator) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
        this.authReqValidator = authReqValidator;
        this.userReqDTOValidator = userReqDTOValidator;
    }

    @Override
    public String loginUser(UserLoginReqDTO userLoginReqDTO) {
        try {
        // Validate request first
        authReqValidator.validate(userLoginReqDTO);

        String loginIdentifier = buildIdentifier(userLoginReqDTO.getEmail(), userLoginReqDTO.getPhoneNumber());

        System.out.println("Login identifier: "+ loginIdentifier);

        if(!validateLoginIdentifier(loginIdentifier))
            throw new InvalidRequestException("Invalid email or phone number");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginIdentifier,
                        userLoginReqDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return token;
        } catch (Exception ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    private String buildIdentifier(String email, String phone) {
        email = (email != null) ? email.trim() : "";
        phone = (phone != null) ? phone.trim() : "";

        if (!email.isEmpty() && !phone.isEmpty()) return email + ":" + phone;  // both
        if (!email.isEmpty()) return email + ":";          // only email
        if (!phone.isEmpty()) return ":" + phone;          // only phone

        throw new IllegalArgumentException("Neither of email or password passed");                  // neither
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
            if(!userRepository.existsByEmailAndPhoneNumberAndVerifiedTrue(email, phoneNumber))
                throw new InvalidRequestException("User account not found or not yet verified");
            return userRepository.existsByEmailAndPhoneNumber(email, phoneNumber);
        } else if (!email.isEmpty()) {
            if(!userRepository.existsByEmailAndVerifiedTrue(email))
                throw new InvalidRequestException("User account not found or not yet verified");
            return userRepository.existsByEmail(email);
        } else if (!phoneNumber.isEmpty()) {
//            if(!userRepository.existsByPhoneNumberAndVerifiedTrue(phoneNumber))
//                throw new InvalidRequestException("User account not found or not yet verified");
            return userRepository.existsByPhoneNumber(phoneNumber);
        }

        return false;
    }

    @Override
    @Transactional
    public User registerUser(UserRegistrationReqDTO userRegistrationReqDTO) {
        try {
            userReqDTOValidator.validate(userRegistrationReqDTO);

            if (userRegistrationReqDTO.getEmail() != null &&
                    userRepository.existsByEmail(userRegistrationReqDTO.getEmail())) {
                throw new InvalidRequestException("Email already in use");
            }

            if (userRegistrationReqDTO.getPhoneNumber() != null &&
                    userRepository.existsByPhoneNumber(userRegistrationReqDTO.getPhoneNumber())) {
                throw new InvalidRequestException("Phone number already in use");
            }

            LocationDTO locationDTO = userRegistrationReqDTO.getLocation();
            Location location = new Location(locationDTO.getDistrict(), locationDTO.getProvince(), locationDTO.getCountry());

            User newUser = modelMapper.map(userRegistrationReqDTO, User.class);

            newUser.setLocation(location);
            var regularUserRole = new Role("ROLE_USER");
            newUser.setRoles(Set.of(regularUserRole));
            newUser.setPassword(passwordEncoder.encode(userRegistrationReqDTO.getPassword()));

            // Mark user as NOT verified initially
            newUser.setVerified(false);

            User savedUser = userRepository.save(newUser);

            // Generate verification token and save it
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(savedUser);
            verificationToken.setExpiryDate(
                    Date.from(LocalDateTime.now().plusHours(24).atZone(ZoneId.systemDefault()).toInstant())
            ); // expire in 24 hours
            verificationTokenRepository.save(verificationToken);

            // Send verification email if email is provided
            if(savedUser.getEmail() != null && !savedUser.getEmail().isEmpty())
                emailService.sendVerificationEmail(savedUser, token);

            return savedUser;
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidRequestException("Registration failed: " + ex.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}