package com.supplylink.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.supplylink.dtos.res.PaymentResponse;
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
    public PaymentResponse processPayment(UUID userId, BigDecimal amount) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("amount", amount.multiply(BigDecimal.valueOf(100)).longValue()); // in cents
            params.put("currency", "usd");
            params.put("payment_method_types", List.of("card"));

            PaymentIntent intent = PaymentIntent.create(params);

            return new PaymentResponse("stripe", intent.getId(), "SUCCESS", amount);
        } catch (StripeException e) {
            return new PaymentResponse("stripe", null, "FAILED", amount);
        }
    }
}
