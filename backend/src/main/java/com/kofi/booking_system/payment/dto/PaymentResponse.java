package com.kofi.booking_system.payment.dto;

import com.kofi.booking_system.payment.enums.PaymentMethod;
import com.kofi.booking_system.payment.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long appointmentId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
