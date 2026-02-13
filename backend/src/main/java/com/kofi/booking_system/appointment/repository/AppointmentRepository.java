package com.kofi.booking_system.appointment.repository;

import com.kofi.booking_system.appointment.enums.TimeSlot;
import com.kofi.booking_system.appointment.model.Appointment;
import com.kofi.booking_system.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    /**
     * Checks if a provider already has a booking
     * for a given date and slot.
     */
    Optional<Appointment> findByAppointmentDateAndTimeSlot(User provider, LocalDate date, TimeSlot time);

    List<Appointment> findByProviderAndAppointmentDate(User provider, LocalDate date);
}
