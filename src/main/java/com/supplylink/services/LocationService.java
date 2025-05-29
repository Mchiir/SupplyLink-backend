package com.supplylink.services;

import com.supplylink.models.Location;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public interface LocationService {
    Location createLocation(Location location);
    List<Location> createLocations(List<Location> locations);
    Location getLocationById(UUID id);
    List<Location> getAllLocations();
    Location updateLocation(UUID id, Location location);
    boolean deleteLocation(UUID id);
}