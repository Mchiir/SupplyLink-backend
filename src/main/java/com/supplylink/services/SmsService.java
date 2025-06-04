package com.supplylink.services;


import com.supplylink.dtos.req.TwilioRequest;

public interface SmsService {
    void sendSms(TwilioRequest request);
    void verifyPhone(String phoneNumber); // For OTP logic if needed
}