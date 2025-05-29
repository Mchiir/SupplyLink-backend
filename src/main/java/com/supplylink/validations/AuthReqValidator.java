package com.supplylink.validations;

import com.supplylink.dtos.req.UserLoginReqDTO;
import com.supplylink.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AuthReqValidator {

    public void validate(UserLoginReqDTO userLoginReqDTO) {
        // Check password is provided
        if (!StringUtils.hasText(userLoginReqDTO.getPassword())) {
            throw new InvalidRequestException("Password is required");
        }

        // Check at least one identifier is provided
        boolean hasEmail = StringUtils.hasText(userLoginReqDTO.getEmail());
        boolean hasPhone = StringUtils.hasText(userLoginReqDTO.getPhoneNumber());

        if (!hasEmail && !hasPhone) {
            throw new InvalidRequestException("Either email or phone number must be provided");
        }

        // Validate email format if provided
        if (hasEmail && !isValidEmail(userLoginReqDTO.getEmail())) {
            throw new InvalidRequestException("Invalid email format");
        }

        // Validate phone format if provided (Rwanda-specific)
        if (hasPhone && !isValidPhoneNumber(userLoginReqDTO.getPhoneNumber())) {
            throw new InvalidRequestException("Phone number must be in format +2507XXXXXXXX or 07XXXXXXXX");
        }
    }

    private boolean isValidEmail(String email) {
        // Basic email pattern check
        return email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Rwanda phone validation (+250 or 0 prefix)
        return phoneNumber.matches("^(\\+250|0)7[0-9]{8}$");
    }
}