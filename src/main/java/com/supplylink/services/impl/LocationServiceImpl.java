package com.supplylink.services.impl;

import com.supplylink.models.Location;
import com.supplylink.repositories.LocationRepository;
import com.supplylink.services.LocationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    @Transactional
    @Override
    public List<Location> createLocations(List<Location> locations) {
        return locationRepository.saveAll(locations);
    }

    @Override
    public Optional<Location> getLocationById(UUID id) {
        return locationRepository.findById(id);
    }

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location updateLocation(UUID id, Location updated) {
        Location location = getLocationById(id)
                .orElseThrow(() -> new RuntimeException("No location found with id: " + id));

        location.setDistrict(updated.getDistrict());
        location.setProvince(updated.getProvince());
        location.setCountry(updated.getCountry());

        return locationRepository.save(location);
    }

    @Override
    public boolean deleteLocation(UUID id) {
        locationRepository.deleteById(id);
        return true;
    }
}