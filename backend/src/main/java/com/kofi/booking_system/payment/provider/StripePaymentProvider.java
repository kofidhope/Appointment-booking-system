package com.kofi.booking_system.payment.provider;

import com.kofi.booking_system.payment.entity.Payment;
import com.kofi.booking_system.payment.enums.PaymentStatus;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripePaymentProvider implements PaymentProvider {

    @Value("${stripe.secret_key}")
    private String stripeSecretKey;

    @PostConstruct
    void init(){
        if (stripeSecretKey == null || stripeSecretKey.isBlank()){
            throw new IllegalStateException("Stripe secret key is missing");
        }
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public boolean supports(String method) {
        return method.equalsIgnoreCase("STRIPE");
    }

    @Override
    public String initiate(Payment payment) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(payment.getAmount().longValue() * 100) // cents
                    .setCurrency("usd")
                    .putMetadata("paymentId", payment.getId().toString())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            payment.setProviderReference(paymentIntent.getId());
            payment.setStatus(PaymentStatus.PENDING);

            return paymentIntent.getClientSecret();

        } catch (Exception e) {
            throw new RuntimeException("Stripe payment failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void confirm(Payment payment, String providerReference) {
        try {
            PaymentIntent intent = PaymentIntent.retrieve(providerReference);

            if ("succeeded".equals(intent.getStatus())) {
                payment.setStatus(PaymentStatus.SUCCESS);
            } else if ("processing".equals(intent.getStatus())) {
                payment.setStatus(PaymentStatus.PENDING);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
            }
        } catch (Exception e) {
            throw new RuntimeException("Stripe confirmation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void refund(Payment payment) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getProviderReference())
                    .build();

            Refund.create(params);
            payment.setStatus(PaymentStatus.REFUNDED);

        } catch (Exception e) {
            throw new RuntimeException("Stripe refund failed: " + e.getMessage());
        }
    }
}
