package com.kofi.booking_system.payment.entity;

import com.kofi.booking_system.appointment.model.Appointment;
import com.kofi.booking_system.payment.enums.PaymentMethod;
import com.kofi.booking_system.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each appointment can have one payment
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    // Amount charged
    @Column(nullable = false)
    private BigDecimal amount;

    // MOCK / CARD / MOBILE_MONEY
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    // PENDING / SUCCESS / FAILED / CANCELLED
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String providerReference; // Stripe chargeId later

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreatedAt(){
        this.createdAt = LocalDateTime.now();
    }
}
