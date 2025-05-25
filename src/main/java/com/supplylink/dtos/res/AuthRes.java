package com.supplylink.dtos.res;

import java.io.Serializable;

public class AuthRes implements Serializable {
    private String accessToken;

    public AuthRes() {}
    public AuthRes(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAccessToken() {
        return accessToken;
    }
}