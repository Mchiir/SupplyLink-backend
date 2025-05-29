package com.supplylink.services;

import com.supplylink.models.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendVerificationEmail(User user, String token) throws MessagingException, UnsupportedEncodingException;
}
