package com.kofi.booking_system.appointment.service;


import com.kofi.booking_system.appointment.dto.AppointmentResponse;
import com.kofi.booking_system.appointment.dto.CreateAppointmentRequest;

public interface AppointmentService {

    AppointmentResponse bookAppointment(CreateAppointmentRequest request,String authenticatedEmail);

}
