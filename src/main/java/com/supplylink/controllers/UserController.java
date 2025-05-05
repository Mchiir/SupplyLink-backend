package com.supplylink.controllers;

import com.supplylink.models.User;
import com.supplylink.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            // Return unauthorized if no user is authenticated or if the principal is the anonymous user
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            // If the principal is UserDetails (which our User class implements)
            // We might need to fetch the full User entity if UserDetails doesn't contain all fields
            String username = ((UserDetails) principal).getUsername();
            Optional<User> userOpt = Optional.ofNullable(userService.findByUsername(username));
            return userOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Authenticated user details not found"));

        } else if (principal instanceof String) {
            // If the principal is just the username string (less common with UserDetails setup)
            String username = (String) principal;
            Optional<User> userOpt = Optional.ofNullable(userService.findByUsername(username));
            return userOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found for username: " + username));
        } else {
            // Handle unexpected principal type
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected principal type: " + principal.getClass().getName());
        }
    }


    @GetMapping("/by-role")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }




    /**
     * Get user statistics by user ID
     * Returns statistics that match the frontend interface:
     * - activeBookings: number of current active bookings
     * - totalBookings: total number of bookings made by the user
     * - rewardsPoints: user's reward points
     *
     * @param userId The ID of the user to retrieve stats for
     * @return User statistics or 404 if user not found
     */
//    @GetMapping("/{userId}/stats")
//    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
//        Optional<User> userOptional = userService.getUserById(userId);
//
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        User user = userOptional.get();
//
//        // Get booking statistics for the user
//        // These methods would need to be implemented in your BookingService
//        List<BusBooking> gotactiveBookings = bookingService.getActiveBookings(userId,"CONFIRMED");
//        int activeBookings = gotactiveBookings.size();
//        int totalBookings = bookingService.CountBusBookingsByUserId(userId);
//
//        // This could be stored in the user object or calculated based on booking history
//        // For now, we'll use a placeholder value
//        int rewardsPoints = calculateRewardsPoints(userId);
//
//        // Create a map of user statistics matching the frontend interface
//        Map<String, Object> stats = new HashMap<>();
//        stats.put("activeBookings", activeBookings);
//        stats.put("totalBookings", totalBookings);
//        stats.put("rewardsPoints", rewardsPoints);
//
//        return ResponseEntity.ok(stats);
//    }

    /**
     * Calculate rewards points for a user based on their booking history
     * This is a placeholder implementation - you'll need to implement your own logic
     */
//    private int calculateRewardsPoints(Long userId) {
//        // Placeholder implementation
//        // In a real application, this would calculate points based on booking history,
//        // user activity, or retrieve from a dedicated rewards service
//        List<BusBooking> bookings = busBookingService.getBookingsByUser(userId);
//        int totalBookings = bookings.size();
//        int rewardsPoints = totalBookings * 10; // Example calculation: 10 points per booking
//        return rewardsPoints;
//         // Default value for demonstration
//    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            // Use Spring Security's authentication manager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // If authentication is successful, set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Find the user by username
            User user = userService.findByUsername(username);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error: " + e.getMessage());
        }
    }


    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user) {
        User updatedUser = userService.updateUser(userId, user);
        return updatedUser != null
            ? ResponseEntity.ok(updatedUser)
            : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        boolean deleted = userService.deleteUser(userId);

        if (deleted) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}