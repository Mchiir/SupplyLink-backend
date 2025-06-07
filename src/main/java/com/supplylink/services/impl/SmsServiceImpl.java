package com.supplylink.services.impl;

import com.supplylink.dtos.req.TwilioRequest;
import com.supplylink.services.SmsService;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendSms(TwilioRequest request) {
        try {
            Message.creator(
                    new PhoneNumber(request.getToPhoneNumber()),
                    new PhoneNumber(request.getFromPhoneNumber()),
                    request.getCode()
            ).create();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void verifyPhone(String phoneNumber) {

    }
}
