package com.supplylink.dtos;

import com.supplylink.models.Location;

import java.util.Set;

public class UserResDTO {

    private String firstName;
    private String lastName;
    private String email;
    private Location location;
    private Set<String> roles; // Simplified role names for response

    public UserResDTO() {
    }

    public UserResDTO(String firstName, String lastName, String email, Location location, Set<String> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.location = location;
        this.roles = roles;
    }

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
