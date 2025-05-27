package com.supplylink.dtos.res;

import java.io.Serializable;

public class UserLoginResDTO implements Serializable {
    private String accessToken;

    public UserLoginResDTO() {}
    public UserLoginResDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAccessToken() {
        return accessToken;
    }
}