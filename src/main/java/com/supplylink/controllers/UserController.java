package com.supplylink.controllers;

import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.req.UserRegistrationReqDTO;
import com.supplylink.dtos.res.UserRegistrationResDTO;
import com.supplylink.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get All Users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserRegistrationResDTO>>> getAllUsers() {
        List<UserRegistrationResDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    // Get Single User
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserRegistrationResDTO>> getUserById(@PathVariable UUID id) {
        try {
            UserRegistrationResDTO user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found: " + ex.getMessage()));
        }
    }

    // Update User
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserRegistrationResDTO>> updateUser(@PathVariable UUID id,
                                                                          @Valid @RequestBody UserRegistrationReqDTO userRegistrationReqDTO) {
        try {
            UserRegistrationResDTO updatedUser = userService.updateUser(id, userRegistrationReqDTO);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Update failed: " + ex.getMessage()));
        }
    }

    // Delete User
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM') OR hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Deletion failed: " + ex.getMessage()));
        }
    }
}