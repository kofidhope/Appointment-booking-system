package com.kofi.booking_system.payment.dto;

import com.kofi.booking_system.payment.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    @NotNull
    private Long appointmentId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;
}
