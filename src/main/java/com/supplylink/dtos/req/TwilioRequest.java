package com.supplylink.dtos.req;

import jakarta.validation.constraints.NotBlank;

public class TwilioRequest {
    @NotBlank
    private String toPhoneNumber;
    @NotBlank
    private String fromPhoneNumber;
    @NotBlank
    private String code;

    public TwilioRequest() {};
    public TwilioRequest(String toPhoneNumber, String fromPhoneNumber, String code) {
        this.toPhoneNumber = toPhoneNumber;
        this.fromPhoneNumber = fromPhoneNumber;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFromPhoneNumber() {
        return fromPhoneNumber;
    }

    public void setFromPhoneNumber(String fromPhoneNumber) {
        this.fromPhoneNumber = fromPhoneNumber;
    }

    public String getToPhoneNumber() {
        return toPhoneNumber;
    }

    public void setToPhoneNumber(String toPhoneNumber) {
        this.toPhoneNumber = toPhoneNumber;
    }
}