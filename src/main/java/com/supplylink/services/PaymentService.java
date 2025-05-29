package com.supplylink.services;

import com.supplylink.dtos.res.PaymentResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public interface PaymentService {
    PaymentResponse processPayment(UUID userId, BigDecimal amount, String currency);
}