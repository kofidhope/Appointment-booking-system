package com.kofi.booking_system.appointment.service;

import com.kofi.booking_system.appointment.dto.AppointmentResponse;
import com.kofi.booking_system.appointment.dto.CreateAppointmentRequest;
import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import com.kofi.booking_system.appointment.enums.TimeSlot;
import com.kofi.booking_system.appointment.model.Appointment;
import com.kofi.booking_system.appointment.repository.AppointmentRepository;
import com.kofi.booking_system.audit.service.AuditLogService;
import com.kofi.booking_system.common.exception.BadRequestException;
import com.kofi.booking_system.common.exception.BookingConflictException;
import com.kofi.booking_system.common.exception.ForbiddenActionException;
import com.kofi.booking_system.common.exception.ResourceNotFoundException;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    private final EmailTemplateService emailTemplateService;
    private final AuditLogService auditLogService;

    @Override
    public AppointmentResponse bookAppointment(CreateAppointmentRequest request, String authenticatedEmail) {
        //1. fetch the user(customer)
        User customer = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        //get the provider
        User provider = userRepository.findById(request.getProviderId())
                .orElseThrow(()-> new ResourceNotFoundException("Provider not found"));
        //conflict check
        boolean exists = appointmentRepository.existsByProviderAndAppointmentDateAndTimeSlotAndStatusIn(
                provider,request.getAppointmentDate(),request.getTimeSlot(),
                List.of(AppointmentStatus.CONFIRMED));
        if (exists){
            throw new BookingConflictException("Slot already booked");
        }
        //create the appointment
        Appointment appointment = Appointment.builder()
                .customer(customer)
                .provider(provider)
                .appointmentDate(request.getAppointmentDate())
                .timeSlot(request.getTimeSlot())
                .status(AppointmentStatus.PENDING)
                .build();
        applyTimeSlot(appointment);
        appointmentRepository.save(appointment);

        String html = emailTemplateService.renderNewBooking(appointment);
        notificationService.sendEmail(
                appointment.getProvider().getEmail(),
                "📥 New Appointment Request",
                html
        );

        // Log the action
        auditLogService.log(
                appointment.getCustomer().getEmail(),          // actor
                "CREATE_BOOKING",                         // action
                "Appointment",                                 // entity type
                appointment.getId(),                           // entity ID
                "Customer booked appointment "
                        + appointment.getAppointmentDate()
                        + " at " + appointment.getTimeSlot()  // details
        );

        return mapToResponse(appointment);
    }

    private void applyTimeSlot(Appointment appt) {
        switch (appt.getTimeSlot()) {
            case SLOT_09_00 -> {
                appt.setStartTime(LocalTime.of(9, 0));
                appt.setEndTime(LocalTime.of(10, 0));
            }

            case SLOT_10_00 -> {
                appt.setStartTime(LocalTime.of(10, 0));
                appt.setEndTime(LocalTime.of(11, 0));
            }

            case SLOT_11_00 -> {
                appt.setStartTime(LocalTime.of(11, 0));
                appt.setEndTime(LocalTime.of(12, 0));
            }

            case SLOT_12_00 -> {
                appt.setStartTime(LocalTime.of(12, 0));
                appt.setEndTime(LocalTime.of(13, 0));
            }

            case SLOT_13_00 -> {
                appt.setStartTime(LocalTime.of(13, 0));
                appt.setEndTime(LocalTime.of(14, 0));
            }

            case SLOT_14_00 -> {
                appt.setStartTime(LocalTime.of(14, 0));
                appt.setEndTime(LocalTime.of(15, 0));
            }

            case SLOT_15_00 -> {
                appt.setStartTime(LocalTime.of(15, 0));
                appt.setEndTime(LocalTime.of(16, 0));
            }

            case SLOT_16_00 -> {
                appt.setStartTime(LocalTime.of(16, 0));
                appt.setEndTime(LocalTime.of(17, 0));
            }
        }
    }

    @Override
    public AppointmentResponse confirmAppointment(Long appointmentId, String providerEmail) {
        //1. fetch the appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        //2.ensure the logged-in provider owns the appointment
        if (!appointment.getProvider().getEmail().equals(providerEmail)) {
            throw new ForbiddenActionException("Not authorized to confirm this appointment");
        }
        //3.only pending appoint can be confirmed
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BadRequestException("Only pending appointments can be confirmed");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        String html = emailTemplateService.renderBookingConfirmed(appointment);

        notificationService.sendEmail(
                appointment.getCustomer().getEmail(),
                "✅ Appointment Confirmed",
                html
        );
        // SMS to customer
        notificationService.sendSms(
                appointment.getCustomer().getPhoneNumber(),
                "Your appointment on " + appointment.getAppointmentDate() +
                        " at " + appointment.getTimeSlot() + " has been CONFIRMED."
        );


        auditLogService.log(
                appointment.getProvider().getEmail(),     // actor
                "CONFIRM_APPOINTMENT",                    // action
                "Appointment",                            // entity type
                appointment.getId(),                      // entity ID
                "Provider confirmed booking for " + appointment.getAppointmentDate() +
                        " at " + appointment.getTimeSlot()
        );

         return mapToResponse(appointment);
    }

    @Override
    public AppointmentResponse rejectAppointment(Long appointmentId, String providerEmail) {
        //1. fetch the appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        //2.ensure the logged-in provider owns the appointment
        if (appointment.getProvider().getEmail().equals(providerEmail)) {
            throw new ForbiddenActionException("Not authorized to reject this appointment");
        }
        //3.only pending appoint can be confirmed
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new BadRequestException("Only pending appointments can be rejected");
        }
        appointment.setStatus(AppointmentStatus.REJECTED);
        appointmentRepository.save(appointment);

        String html = emailTemplateService.renderBookingCancelled(appointment);
        notificationService.sendEmail(
                appointment.getCustomer().getEmail(),
                "❌ Appointment Rejected",
                html
        );

        auditLogService.log(
                appointment.getProvider().getEmail(),
                "REJECT_APPOINTMENT",
                "Appointment",
                appointment.getId(),
                "Provider rejected booking for " + appointment.getAppointmentDate() +
                        " at " + appointment.getTimeSlot()
        );


        return mapToResponse(appointment);
    }

    @Override
    public AppointmentResponse cancelAppointment(Long appointmentId, String email,String role) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        //prevent cancelling already finished states
        if (appointment.getStatus()==AppointmentStatus.CANCELLED || appointment.getStatus()==AppointmentStatus.REJECTED){
            throw new BadRequestException("Appointment already closed");
        }
        //customer rule
        if (role.equals("CUSTOMER")) {
            if (!appointment.getCustomer().getEmail().equals(email)) {
                throw new ForbiddenActionException("Not your appointment");
            }
            if (appointment.getAppointmentDate().isBefore(LocalDate.now())){
                throw new BadRequestException("Cannot cancel past appointment");
            }
        }
        //provider rule
        if (role.equals("SERVICE_PROVIDER")){
            if (!appointment.getProvider().getEmail().equals(email)) {
                throw new ForbiddenActionException("Not your appointment");
            }
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        String html = emailTemplateService.renderBookingCancelled(appointment);

        notificationService.sendEmail(appointment.getCustomer().getEmail(), "❌ Appointment Cancelled", html);
        notificationService.sendEmail(appointment.getProvider().getEmail(), "❌ Appointment Cancelled", html);

        notificationService.sendSms(
                appointment.getCustomer().getPhoneNumber(),
                "Your appointment on " + appointment.getAppointmentDate() +
                        " at " + appointment.getTimeSlot() + " has been CANCELLED."
        );

        auditLogService.log(
                email,
                "CANCEL_APPOINTMENT",
                "Appointment",
                appointment.getId(),
                role + " cancelled appointment scheduled on " + appointment.getAppointmentDate()
        );

        return mapToResponse(appointment);

    }

    @Override
    public List<TimeSlot> getAvailableSlots(Long providerId, LocalDate date) {
        // fetch the provider
        User provider = userRepository.findById(providerId)
                .orElseThrow(()-> new ResourceNotFoundException("provider not found"));

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
