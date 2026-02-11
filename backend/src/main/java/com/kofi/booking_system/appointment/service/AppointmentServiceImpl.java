package com.kofi.booking_system.appointment.service;

import com.kofi.booking_system.appointment.dto.AppointmentResponse;
import com.kofi.booking_system.appointment.dto.CreateAppointmentRequest;
import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import com.kofi.booking_system.appointment.model.Appointment;
import com.kofi.booking_system.appointment.repository.AppointmentRepository;
import com.kofi.booking_system.auth.model.User;
import com.kofi.booking_system.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public AppointmentResponse bookAppointment(CreateAppointmentRequest request, String authenticatedEmail) {
        //1. fetch the user(customer)
        User customer = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        //get the provider
        User provider = userRepository.findById(request.getProviderId())
                .orElseThrow(()-> new RuntimeException("Provider not found"));
        //conflict check
        appointmentRepository.findByAppointmentDateAndTimeSlot(
                provider,request.getAppointmentDate(),request.getTimeSlot()
        ).ifPresent(a->{
            throw new RuntimeException("Slot already booked");
        });
        //create the appointment
        Appointment appointment = Appointment.builder()
                .customer(customer)
                .provider(provider)
                .appointmentDate(request.getAppointmentDate())
                .timeSlot(request.getTimeSlot())
                .status(AppointmentStatus.PENDING)
                .build();

        appointmentRepository.save(appointment);

        // Return response
        AppointmentResponse response = new AppointmentResponse();
        response.id = appointment.getId();
        response.date = appointment.getAppointmentDate();
        response.timeSlot = appointment.getTimeSlot();
        response.status = appointment.getStatus();

        return response;
    }
}
