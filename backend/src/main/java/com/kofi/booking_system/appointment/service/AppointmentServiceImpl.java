package com.kofi.booking_system.appointment.service;

import com.kofi.booking_system.appointment.dto.AppointmentResponse;
import com.kofi.booking_system.appointment.dto.CreateAppointmentRequest;
import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import com.kofi.booking_system.appointment.enums.TimeSlot;
import com.kofi.booking_system.appointment.model.Appointment;
import com.kofi.booking_system.appointment.repository.AppointmentRepository;
import com.kofi.booking_system.auth.model.User;
import com.kofi.booking_system.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

        return mapToResponse(appointment);
    }

    @Override
    public AppointmentResponse confirmAppointment(Long appointmentId, String providerEmail) {
        //1. fetch the appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        //2.ensure the logged-in provider owns the appointment
        if (appointment.getProvider().getEmail().equals(providerEmail)) {
            throw new RuntimeException("Not authorized to confirm this appointment");
        }
        //3.only pending appoint can be confirmed
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new RuntimeException("Only pending appointments can be confirmed");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);
         return mapToResponse(appointment);
    }

    @Override
    public AppointmentResponse rejectAppointment(Long appointmentId, String providerEmail) {
        //1. fetch the appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        //2.ensure the logged-in provider owns the appointment
        if (appointment.getProvider().getEmail().equals(providerEmail)) {
            throw new RuntimeException("Not authorized to reject this appointment");
        }
        //3.only pending appoint can be confirmed
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new RuntimeException("Only pending appointments can be rejected");
        }
        appointment.setStatus(AppointmentStatus.REJECTED);
        appointmentRepository.save(appointment);
        return mapToResponse(appointment);
    }

    @Override
    public AppointmentResponse cancelAppointment(Long appointmentId, String email,String role) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        //prevent cancelling already finished states
        if (appointment.getStatus()==AppointmentStatus.CANCELLED || appointment.getStatus()==AppointmentStatus.REJECTED){
            throw new RuntimeException("Appointment already closed");
        }
        //customer rule
        if (role.equals("CUSTOMER")) {
            if (!appointment.getCustomer().getEmail().equals(email)) {
                throw new RuntimeException("Not your appointment");
            }
            if (appointment.getAppointmentDate().isBefore(LocalDate.now())){
                throw new RuntimeException("Cannot cancel past appointment");
            }
        }
        //provider rule
        if (role.equals("CUSTOMER")){
            if (!appointment.getProvider().getEmail().equals(email)) {
                throw new RuntimeException("Not your appointment");
            }
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        return mapToResponse(appointment);

    }

    @Override
    public List<TimeSlot> getAvailabilitySlots(Long providerId, LocalDate date) {
        // fetch the provider
        User provider = userRepository.findById(providerId)
                .orElseThrow(()-> new RuntimeException("provider not found"));

        //get all booking for the day
        List<Appointment> appointments = appointmentRepository.findByProviderAndAppointmentDate(provider, date);

        //extract taken slot
        List<TimeSlot> takenSlots = appointments.stream()
                .filter(a-> a.getStatus() == AppointmentStatus.PENDING
                        || a.getStatus() == AppointmentStatus.CONFIRMED
                )
                .map(Appointment::getTimeSlot)
                .toList();

        //return the available ones
        return Arrays.stream(TimeSlot.values())
                .filter(slot-> !takenSlots.contains(slot))
                .toList();
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .date(appointment.getAppointmentDate())
                .timeSlot(appointment.getTimeSlot())
                .status(appointment.getStatus())
                .build();
    }
}
