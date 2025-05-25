package com.supplylink.dtos.req;

public class UserPayload {
    private String email;
    private String phoneNumber;
    private String userId;

    public UserPayload(String email, String phoneNumber, String userId) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserId() {
        return userId;
    }
}
