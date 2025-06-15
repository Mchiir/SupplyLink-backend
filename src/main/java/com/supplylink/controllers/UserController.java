package com.supplylink.controllers;

import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.req.UserRegistrationReqDTO;
import com.supplylink.dtos.res.UserRegistrationResDTO;
import com.supplylink.models.User;
import com.supplylink.services.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    // Get All Users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserRegistrationResDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users = userService.getAllUsers(pageable);
        Page<UserRegistrationResDTO> paginatedResponse = users.map(user -> modelMapper.map(user, UserRegistrationResDTO.class));

        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", paginatedResponse));
    }

    // Get Single User
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserRegistrationResDTO>> getUserById(@PathVariable UUID id) {
        try {
            UserRegistrationResDTO user = modelMapper.map(userService.getUserById(id), UserRegistrationResDTO.class);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found with ID: " + id));
            }
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
            UserRegistrationResDTO updatedUser = modelMapper.map(
                    userService.updateUser(id, userRegistrationReqDTO), UserRegistrationResDTO.class);
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