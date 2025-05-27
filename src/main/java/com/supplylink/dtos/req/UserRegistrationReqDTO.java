package com.supplylink.dtos.req;

import com.supplylink.dtos.LocationDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRegistrationReqDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 30, message = "First name must be between 2 and 30 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 30, message = "Last name must be between 2 and 30 characters")
    private String lastName;

    private String email;

    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 60, message = "Password must be between 8 and 60 characters")
    private String password;

    @NotNull
    private LocationDTO locationDTO;

    public UserRegistrationReqDTO() {}

    public UserRegistrationReqDTO(String firstName, String lastName, String email, String phoneNumber, String password, LocationDTO locationDTO) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.locationDTO = locationDTO;
    }

    // Getters and Setters

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocationDTO getLocationDTO() {
        return locationDTO;
    }

    public void setLocationDTO(LocationDTO location) {
        this.locationDTO = location;
    }
}