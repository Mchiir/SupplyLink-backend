package com.supplylink.controllers;

import com.supplylink.models.Location;
import com.supplylink.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping
    public List<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable UUID id) {
        return locationService.getLocationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Location createLocation(@RequestBody Location location) {
        return locationService.createLocation(location);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(@PathVariable UUID id, @RequestBody Location location) {
        var updatedLocation = Optional.ofNullable(locationService.updateLocation(id, location));
        return  updatedLocation
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
        if (locationService.deleteLocation(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
