package com.supplylink.validations;

import com.supplylink.dtos.req.UserRegistrationReqDTO;
import com.supplylink.exceptions.InvalidRequestException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserReqDTOValidator {

    public void validate(UserRegistrationReqDTO userRegistrationReqDTO) {
        // Check at least one contact method is provided
        boolean hasEmail = StringUtils.hasText(userRegistrationReqDTO.getEmail());
        boolean hasPhone = StringUtils.hasText(userRegistrationReqDTO.getPhoneNumber());

        if (!hasEmail && !hasPhone) {
            throw new InvalidRequestException("Either email or phone number must be provided");
        }

        // Validate email format if provided
        if (hasEmail && !isValidEmail(userRegistrationReqDTO.getEmail())) {
            throw new InvalidRequestException("Invalid email format");
        }

        // Validate phone format if provided (example for Rwanda)
        if (hasPhone && !isValidPhoneNumber(userRegistrationReqDTO.getPhoneNumber())) {
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