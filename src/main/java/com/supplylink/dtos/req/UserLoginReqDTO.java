package com.supplylink.dtos.req;

import jakarta.validation.constraints.NotBlank;

public class UserLoginReqDTO {
    private String email;

    private String phoneNumber;

    @NotBlank
    private String password;

    public UserLoginReqDTO() {}
    public UserLoginReqDTO(String email, String phoneNumber, String password) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}