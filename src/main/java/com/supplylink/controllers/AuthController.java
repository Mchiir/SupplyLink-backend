package com.supplylink.controllers;

import com.supplylink.dtos.LoginRequest;
import com.supplylink.dtos.LoginResponse;
import com.supplylink.models.User;
import com.supplylink.security.JwtUtil;
import com.supplylink.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for email: " + loginRequest.getEmail());

            // 1. Find the user by email
            UserDetails userDetails = null;
            User fullUser = null;
            try {
                userDetails = userService.loadUserByUsername(loginRequest.getEmail());
                fullUser = userService.findByEmail(loginRequest.getEmail());
                logger.info("User found: " + userDetails.getUsername());

                // Debug: Log encoded password details
                logger.info("Stored password hash: " + userDetails.getPassword());
                String testEncode = passwordEncoder.encode(loginRequest.getPassword());
                logger.info("Test encoding of provided password: " + testEncode);
                logger.info("Password encoder class: " + passwordEncoder.getClass().getName());
            } catch (UsernameNotFoundException e) {
                logger.warning("User not found with email: " + loginRequest.getEmail());
                return ResponseEntity.badRequest().body("User not found");
            }

            // 2. Check password manually
            if (userDetails == null) {
                logger.warning("UserDetails is null after loadUserByUsername");
                return ResponseEntity.badRequest().body("User not found");
            }

            // 3. Use passwordEncoder to verify the password
            logger.info("Verifying password for user: " + userDetails.getUsername());
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword());
            logger.info("Password match result: " + passwordMatches);

            if (!passwordMatches) {
                logger.warning("Password verification failed for user: " + userDetails.getUsername());

                // TEMPORARY DEBUG CODE - REMOVE IN PRODUCTION
                // This is just for debugging - allows any password for testing
                if (loginRequest.getPassword().equals("debug_override_123!")) {
                    logger.warning("DEBUG MODE: Allowing login with debug override password");
                    passwordMatches = true;
                } else {
                    throw new BadCredentialsException("Invalid password");
                }
            }

            logger.info("Password verified successfully for user: " + userDetails.getUsername());

            // 4. If we get here, authentication was successful
            final String jwt = jwtUtil.generateToken(userDetails);
            logger.info("JWT token generated for user: " + userDetails.getUsername());

            // 5. Get the full user object to include in the response
            return ResponseEntity.ok(new LoginResponse(jwt, fullUser != null ? fullUser.getRole() : "USER", fullUser));
        } catch (DisabledException e) {
            logger.warning("User is disabled: " + e.getMessage());
            return ResponseEntity.badRequest().body("User is disabled");
        } catch (BadCredentialsException e) {
            logger.warning("Bad credentials: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid email or password");
        } catch (Exception e) {
            logger.severe("Authentication error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Authentication error: " + e.getMessage());
        }
    }



//    @PostMapping("/company/login")
//    public ResponseEntity<?> companyLogin(@RequestBody LoginRequest loginRequest) {
//        try {
//            // Check if email is null or empty
//            if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
//                logger.warning("Company login attempt with null or empty email");
//                return ResponseEntity.badRequest().body("Email is required");
//            }
//
//            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
//                logger.warning("Company login attempt with null or empty password");
//                return ResponseEntity.badRequest().body("Password is required");
//            }
//
//            logger.info("Company login attempt for email: " + loginRequest.getEmail());
//
//            // Find the company by email
//            BusCompany company = busCompanyService.findByContactEmail(loginRequest.getEmail());
//
//            if (company == null) {
//                logger.warning("Company not found with email: " + loginRequest.getEmail());
//                return ResponseEntity.badRequest().body("Company not found");
//            }
//
//            logger.info("Company found: " + company.getCompanyName());
//
//            // Verify password
//            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), company.getPassword());
//            // If company passwords are stored in plain text, use this instead:
//            // boolean passwordMatches = loginRequest.getPassword().equals(company.getPassword());
//
//            logger.info("Password match result: " + passwordMatches);
//
//            if (!passwordMatches) {
//                logger.warning("Password verification failed for company: " + company.getCompanyName());
//                return ResponseEntity.badRequest().body("Invalid password");
//            }
//
//            // Generate JWT token
//            final String jwt = jwtUtil.generateTokenForCompany(company);
//            logger.info("JWT token generated for company: " + company.getCompanyName());
//
//            // Return success response with token
//            Map<String, Object> response = Map.of(
//                    "token", jwt,
//                    "role", "COMPANY",
//                    "company", company
//            );
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            logger.severe("Company authentication error: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body("Authentication error: " + e.getMessage());
//        }
//    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Admin login attempt for email: " + loginRequest.getEmail());

            // 1. Find the admin by email
            UserDetails userDetails = null;
            try {
                userDetails = userService.loadUserByUsername(loginRequest.getEmail());
                logger.info("Admin found: " + userDetails.getUsername());
            } catch (UsernameNotFoundException e) {
                logger.warning("Admin not found with email: " + loginRequest.getEmail());
                return ResponseEntity.badRequest().body("Admin not found");
            }

            // 2. Check password manually
            if (userDetails == null) {
                logger.warning("UserDetails is null after loadUserByUsername");
                return ResponseEntity.badRequest().body("Admin not found");
            }

            // 3. Use passwordEncoder to verify the password
            logger.info("Verifying password for admin: " + userDetails.getUsername());
            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword());
            logger.info("Password match result: " + passwordMatches);
            String providedpass = passwordEncoder.encode(loginRequest.getPassword());
            logger.info("user password: "+userDetails.getPassword());
            logger.info("provided password: "+providedpass);

            if (!passwordMatches) {
                logger.warning("Password verification failed for admin: " + userDetails.getUsername());

                // TEMPORARY DEBUG CODE - REMOVE IN PRODUCTION
                if (loginRequest.getPassword().equals("debug_override_123!")) {
                    logger.warning("DEBUG MODE: Allowing login with debug override password");
                    passwordMatches = true;
                } else {
                    throw new BadCredentialsException("Invalid password");
                }
            }

            logger.info("Password verified successfully for admin: " + userDetails.getUsername());

            // 4. ticketIf we get here, authentication was successful
            final String jwt = jwtUtil.generateToken(userDetails);
            logger.info("JWT token generated for admin: " + userDetails.getUsername());

            // 5. Get the full user object to include in the response
            User user = userService.findByEmail(loginRequest.getEmail());

            return ResponseEntity.ok(new LoginResponse(jwt, "ADMIN", user));
        } catch (DisabledException e) {
            logger.warning("Admin is disabled: " + e.getMessage());
            return ResponseEntity.badRequest().body("Admin is disabled");
        } catch (BadCredentialsException e) {
            logger.warning("Bad credentials: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid email or password");
        } catch (UsernameNotFoundException e) {
            logger.warning("Admin not found: " + e.getMessage());
            return ResponseEntity.badRequest().body("Admin not found");
        } catch (Exception e) {
            logger.severe("Authentication error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Authentication error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> requestMap) {
        try {
            System.out.println("Received request to /api/auth/register");
            System.out.println("Request data: " + requestMap);

            // Manually extract fields from the request
            String username = (String) requestMap.get("username");
            String email = (String) requestMap.get("email");
            String password = (String) requestMap.get("password");
            String role = (String) requestMap.get("role");
            String firstName = (String) requestMap.get("firstName");
            String lastName = (String) requestMap.get("lastName");
            String phoneNumber = (String) requestMap.get("phoneNumber");

            // Handle isActive - could be sent as is_active or isActive
            Boolean isActive = null;
            if (requestMap.containsKey("isActive")) {
                isActive = (Boolean) requestMap.get("isActive");
            } else if (requestMap.containsKey("is_active")) {
                isActive = (Boolean) requestMap.get("is_active");
            }

            // Validate required fields
            if (username == null || email == null || password == null) {
                return ResponseEntity.badRequest().body(
                        java.util.Collections.singletonMap("message", "Username, email, and password are required")
                );
            }

            System.out.println("Registering user: " + username + ", " + email);

            // Check for duplicate username
            if (userService.findByUsername(username) != null) {
                return ResponseEntity.badRequest().body(
                        java.util.Collections.singletonMap("message", "Username already exists")
                );
            }

            // Check for duplicate email
            if (userService.findByEmail(email) != null) {
                return ResponseEntity.badRequest().body(
                        java.util.Collections.singletonMap("message", "Email already exists")
                );
            }

            // Create a new User object
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role != null ? role : "USER"); // Default to USER if not specified
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setActive(isActive != null ? isActive : true); // Default to true if not specified

            User savedUser = userService.createUser(user);
            System.out.println("User registered: " + savedUser.getUsername());

            // Generate JWT token for the newly registered user
            // Create UserDetails from the saved user
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    savedUser.getEmail(),
                    savedUser.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + savedUser.getRole()))
            );

            final String jwt = jwtUtil.generateToken(userDetails);

            // Return the same response format as login
            return ResponseEntity.ok(new LoginResponse(jwt, savedUser.getRole(), savedUser));
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    java.util.Collections.singletonMap("message", "Error registering user: " + e.getMessage())
            );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String newPassword = request.get("newPassword");

            if (email == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Email and newPassword are required");
            }

            User user = userService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found with email: " + email);
            }

            logger.info("new password going to be set "+ newPassword);
            logger.info("for user "+ email);
            logger.info("Current password hash: " + user.getPassword());


            // Encode the new password and update the user
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userService.updateUser(user.getId(), user);

            logger.info("Password reset for user: " + user.getUsername());
            logger.info("New password hash: " + encodedPassword);

            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            logger.severe("Error resetting password: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error resetting password: " + e.getMessage());
        }
    }
}