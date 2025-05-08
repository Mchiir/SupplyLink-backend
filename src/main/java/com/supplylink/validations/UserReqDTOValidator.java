package com.supplylink.validations;

import com.supplylink.dtos.UserReqDTO;
import com.supplylink.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserReqDTOValidator {

    public void validate(UserReqDTO userReqDTO) {
        // Check required fields
        if (!StringUtils.hasText(userReqDTO.getFirstName())) {
            throw new InvalidRequestException("First name is required");
        }

        if (!StringUtils.hasText(userReqDTO.getLastName())) {
            throw new InvalidRequestException("Last name is required");
        }

        if (!StringUtils.hasText(userReqDTO.getPassword())) {
            throw new InvalidRequestException("Password is required");
        }

        // Validate length constraints
        if (userReqDTO.getFirstName().length() < 3 || userReqDTO.getFirstName().length() > 30) {
            throw new InvalidRequestException("First name must be between 3 and 30 characters");
        }

        if (userReqDTO.getLastName().length() < 3 || userReqDTO.getLastName().length() > 30) {
            throw new InvalidRequestException("Last name must be between 3 and 30 characters");
        }

        if (userReqDTO.getPassword().length() < 8 || userReqDTO.getPassword().length() > 60) {
            throw new InvalidRequestException("Password must be between 8 and 60 characters");
        }

        // Check at least one contact method is provided
        boolean hasEmail = StringUtils.hasText(userReqDTO.getEmail());
        boolean hasPhone = StringUtils.hasText(userReqDTO.getPhoneNumber());

        if (!hasEmail && !hasPhone) {
            throw new InvalidRequestException("Either email or phone number must be provided");
        }

        // Validate email format if provided
        if (hasEmail && !isValidEmail(userReqDTO.getEmail())) {
            throw new InvalidRequestException("Invalid email format");
        }

        // Validate phone format if provided (example for Rwanda)
        if (hasPhone && !isValidPhoneNumber(userReqDTO.getPhoneNumber())) {
            throw new InvalidRequestException("Phone number must be in format +2507XXXXXXXX or 07XXXXXXXX");
        }
    }

    private boolean isValidEmail(String email) {
        // Simple email regex pattern
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Rwanda phone number validation
        return phoneNumber.matches("^(\\+250|0)7[0-9]{8}$");
    }
}