package com.kofi.booking_system.payment.controller;

import com.kofi.booking_system.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/webhook/paystack")
@RequiredArgsConstructor
public class PaystackWebhookController {

    private final PaymentService paymentService;

    // Paystack will POST to this endpoint after a user pays
    @PostMapping
    public ResponseEntity<String> handlePaystackWebhook(
            @RequestHeader("x-paystack-signature") String signature, // Paystack security header
            @RequestBody String payload // raw JSON body
    ) {
        paymentService.handlePaystackWebhook(signature, payload);
        return ResponseEntity.ok("Webhook received");
    }
}
