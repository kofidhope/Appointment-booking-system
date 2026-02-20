package com.kofi.booking_system.payment.service;

import com.kofi.booking_system.payment.dto.CreatePaymentRequest;
import com.kofi.booking_system.payment.dto.PaymentInitResponse;
import com.kofi.booking_system.payment.dto.PaymentResponse;

public interface PaymentService {

    PaymentInitResponse createPayment(CreatePaymentRequest request, String customerEmail);

    PaymentResponse confirmPayment(Long paymentId, String providerReference);

    void handlePaystackWebhook(String signature, String payload);

    PaymentResponse refundPayment(Long paymentId, String email);
}
