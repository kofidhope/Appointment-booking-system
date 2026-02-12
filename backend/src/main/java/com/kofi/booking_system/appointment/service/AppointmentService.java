package com.kofi.booking_system.appointment.service;


import com.kofi.booking_system.appointment.dto.AppointmentResponse;
import com.kofi.booking_system.appointment.dto.CreateAppointmentRequest;

public interface AppointmentService {

    AppointmentResponse bookAppointment(CreateAppointmentRequest request,String authenticatedEmail);

    AppointmentResponse confirmAppointment(Long appointmentId, String providerEmail);

    AppointmentResponse rejectAppointment(Long appointmentId, String providerEmail);

    AppointmentResponse cancelAppointment(Long appointmentId, String providerEmail, String role);


}
