package com.supplylink.controllers;

import com.supplylink.dtos.LocationDTO;
import com.supplylink.dtos.req.ProductReqDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.dtos.res.ProductResDTO;
import com.supplylink.models.Location;
import com.supplylink.models.Product;
import com.supplylink.services.LocationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
@PreAuthorize("hasRole('ADMIN') OR hasRole('USER')")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getAllLocations() {
        List<LocationDTO> result = locationService.getAllLocations().stream()
                .map(location -> modelMapper.map(location, LocationDTO.class))
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Locations fetched", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationDTO>> getLocationById(@PathVariable UUID id) {
        return Optional.ofNullable(locationService.getLocationById(id))
                .map(loc -> ResponseEntity.ok(ApiResponse.success("Found", modelMapper.map(loc, LocationDTO.class))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Location not found")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LocationDTO>> createLocation(@Valid @RequestBody LocationDTO locationDTO) {
        try {
            Location saved = locationService.createLocation(modelMapper.map(locationDTO, Location.class));
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Created", modelMapper.map(saved, LocationDTO.class)));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Creation failed: " + ex.getMessage()));
        }
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> createLocations(@Valid @RequestBody List<LocationDTO> dtos) {
        try {
            var locations = dtos.stream().map(dto -> modelMapper.map(dto, Location.class)).collect(Collectors.toList());
            var created = locationService.createLocations(locations)
                    .stream()
                    .map(p -> modelMapper.map(p, LocationDTO.class))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Batch created", created));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error("Batch failed: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationDTO>> updateLocation(@PathVariable UUID id, @Valid @RequestBody LocationDTO locationDTO) {
        try {
            Location updated = locationService.updateLocation(id, modelMapper.map(locationDTO, Location.class));
            return ResponseEntity.ok(ApiResponse.success("Updated", modelMapper.map(updated, LocationDTO.class)));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Update failed: " + ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteLocation(@PathVariable UUID id) {
        if (locationService.deleteLocation(id)) {
            return ResponseEntity.ok(ApiResponse.success("Deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Location not found"));
    }
}
