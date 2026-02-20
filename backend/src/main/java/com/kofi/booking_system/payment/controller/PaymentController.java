package com.kofi.booking_system.payment.controller;

import com.kofi.booking_system.payment.dto.CreatePaymentRequest;
import com.kofi.booking_system.payment.dto.PaymentInitResponse;
import com.kofi.booking_system.payment.dto.PaymentResponse;
import com.kofi.booking_system.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentInitResponse> createPayment(
            @RequestBody @Valid CreatePaymentRequest request, Authentication authentication) {
        return ResponseEntity.ok(paymentService.createPayment(request,authentication.getName()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmPayment(@RequestParam Long paymentId, @RequestParam String providerRef) {
        paymentService.confirmPayment(paymentId, providerRef);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Long paymentId,Authentication authentication) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId,authentication.getName()));
    }

}
