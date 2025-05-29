package com.supplylink.services.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.supplylink.dtos.res.PaymentResponse;
import com.supplylink.models.enums.PaymentStatus;
import com.supplylink.services.PaymentService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("stripePaymentService")
public class StripePaymentService implements PaymentService {

    private final Dotenv dotenv;
    private final String stripeSecretKey;

    public StripePaymentService() {
        dotenv = Dotenv.load();
        stripeSecretKey = dotenv.get("STRIPE_API_KEY");
    }


    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public PaymentResponse processPayment(UUID userId, BigDecimal amount, String currency) {
        final int CENT_MULTIPLIER = 100;
        final String PAYMENT_METHOD_CARD = "card";
        final String PROVIDER_NAME = "stripe";
        try {

            Map<String, Object> params = new HashMap<>();
            params.put("amount", amount.multiply(BigDecimal.valueOf(CENT_MULTIPLIER)).longValue()); // in cents
            params.put("currency", currency);
            params.put("payment_method_types", List.of(PAYMENT_METHOD_CARD));

            PaymentIntent intent = PaymentIntent.create(params);

            return new PaymentResponse(PROVIDER_NAME, intent.getId(), PaymentStatus.SUCCEEDED, amount, currency);
        } catch (StripeException e) {
            return new PaymentResponse(PROVIDER_NAME, null, PaymentStatus.FAILED, amount, currency);
        }
    }
}
