package com.kofi.booking_system.appointment.repository;

import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import com.kofi.booking_system.appointment.enums.TimeSlot;
import com.kofi.booking_system.appointment.model.Appointment;
import com.kofi.booking_system.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    /**
     * Checks if a provider already has a booking
     * for a given date and slot.
     */
    boolean existsByProviderAndAppointmentDateAndTimeSlotAndStatusIn(
            User provider,
            LocalDate date,
            TimeSlot time,
            List<AppointmentStatus> status
    );

    List<Appointment> findByProviderAndAppointmentDate(User provider, LocalDate date);

    Slice<Appointment> findByStatusAndCreatedAtBefore(AppointmentStatus status, LocalDateTime time, Pageable pageable);

    long countByAppointmentDate(LocalDate date);

    long countByAppointmentDateAndStatus(LocalDate date, AppointmentStatus status);

}
