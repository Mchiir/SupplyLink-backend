package com.supplylink.controllers;

import com.supplylink.dtos.LocationDTO;
import com.supplylink.dtos.res.ApiResponse;
import com.supplylink.models.Location;
import com.supplylink.services.LocationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
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
        return locationService.getLocationById(id)
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
