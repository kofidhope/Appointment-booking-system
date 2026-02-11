package com.kofi.booking_system.appointment.dto;

import com.kofi.booking_system.appointment.enums.TimeSlot;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateAppointmentRequest {

    private Long providerId;
    private LocalDate appointmentDate;
    private TimeSlot timeSlot;

}
