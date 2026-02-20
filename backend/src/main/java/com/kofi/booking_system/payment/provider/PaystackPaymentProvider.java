package com.kofi.booking_system.payment.provider;

import com.kofi.booking_system.payment.dto.PaystackInitResponse;
import com.kofi.booking_system.payment.dto.PaystackVerifyResponse;
import com.kofi.booking_system.payment.entity.Payment;
import com.kofi.booking_system.payment.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaystackPaymentProvider implements PaymentProvider {

    private final WebClient paystackClient;

    @Override
    public boolean supports(String method) {
        return method.equalsIgnoreCase("PAYSTACK");
    }

    @Override
    public String initiate(Payment payment) {

        Map<String,Object> body = Map.of(
                "email",payment.getAppointment().getCustomer().getEmail(),
                "amount", payment.getAmount().multiply(BigDecimal.valueOf(100)).intValue(),
                "currency", "GHS",
                "reference", "PAY" + payment.getId()
        );

        PaystackInitResponse response = paystackClient.post()
                .uri("/transaction/initialize")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PaystackInitResponse.class)
                .block();

        if (response == null || response.getData() == null) {
            throw new RuntimeException("Failed to initialize Paystack payment");
        }

        payment.setProviderReference(body.get("reference").toString());
        payment.setStatus(PaymentStatus.PENDING);

        return response.getData().getAuthorization_url();
    }

    @Override
    public void confirm(Payment payment, String providerReference) {

        PaystackVerifyResponse response = paystackClient.get()
                .uri("/transaction/verify/{ref}", providerReference)
                .retrieve()
                .bodyToMono(PaystackVerifyResponse.class)
                .block();

        if (response != null && response.getData() != null
                && "success".equalsIgnoreCase(response.getData().getStatus())) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
    }

    @Override
    public void refund(Payment payment) {

        Map<String, Object> body = Map.of(
                "transaction", payment.getProviderReference()
        );

        paystackClient.post()
                .uri("/refund")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        payment.setStatus(PaymentStatus.REFUNDED);
    }
}
