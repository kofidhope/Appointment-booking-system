package com.kofi.booking_system.appointment.service;


import com.kofi.booking_system.appointment.dto.AppointmentResponse;
import com.kofi.booking_system.appointment.dto.CreateAppointmentRequest;
import com.kofi.booking_system.appointment.enums.TimeSlot;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    AppointmentResponse bookAppointment(CreateAppointmentRequest request,String authenticatedEmail);

    AppointmentResponse confirmAppointment(Long appointmentId, String providerEmail);

    AppointmentResponse rejectAppointment(Long appointmentId, String providerEmail);

    AppointmentResponse cancelAppointment(Long appointmentId, String providerEmail, String role);

    List<TimeSlot> getAvailabilitySlots(Long providerId, LocalDate date);
}
