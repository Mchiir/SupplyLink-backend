package com.supplylink.controllers;

import ClickSend.Api.SmsApi;
import ClickSend.ApiClient;
import ClickSend.ApiException;
import ClickSend.Model.SmsMessage;
import ClickSend.Model.SmsMessageCollection;
import com.supplylink.dtos.req.MessageDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sms")
public class SmsRestController {
    @Autowired
    private ApiClient clickSendConfig;

    @PostMapping("/sms1000")
    public ResponseEntity<String> sendSMStoUpto1000Numbers(@Valid @RequestBody MessageDetails messageDetails) {
        SmsApi smsApi = new SmsApi(clickSendConfig);

        SmsMessage smsMessage = new SmsMessage();
        smsMessage.body(messageDetails.getMessageBody());
        smsMessage.listId(messageDetails.getListId());
        smsMessage.source(messageDetails.getSendingSource());

        List<SmsMessage> smsMessageList = List.of(smsMessage);

        // SmsMessageCollection | SmsMessageCollection model
        SmsMessageCollection smsMessages = new SmsMessageCollection();
        smsMessages.messages(smsMessageList);

        try {
            String result = smsApi.smsSendPost(smsMessages);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ApiException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>("Exception when calling SmsApi#smsSendPost", HttpStatus.BAD_REQUEST);
    }
}
