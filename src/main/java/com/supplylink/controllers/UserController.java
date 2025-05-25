package com.supplylink.controllers;

import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.req.UserReqDTO;
import com.supplylink.dtos.res.UserResDTO;
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
    public ResponseEntity<ApiResponse<List<UserResDTO>>> getAllUsers() {
        List<UserResDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    // Get Single User
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResDTO>> getUserById(@PathVariable UUID id) {
        try {
            UserResDTO user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found: " + ex.getMessage()));
        }
    }

    // Update User
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResDTO>> updateUser(@PathVariable UUID id,
                                                              @Valid @RequestBody UserReqDTO userReqDTO) {
        try {
            UserResDTO updatedUser = userService.updateUser(id, userReqDTO);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Update failed: " + ex.getMessage()));
        }
    }

    // Delete User
    @DeleteMapping("/{id}")
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