package com.kofi.booking_system.payment.repository;

import com.kofi.booking_system.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.ScopedValue;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByAppointmentId(Long appointmentId);

    Optional<Payment> findByProviderReference(String reference);
}
