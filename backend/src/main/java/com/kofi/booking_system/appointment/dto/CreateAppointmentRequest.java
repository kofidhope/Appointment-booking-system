package com.kofi.booking_system.appointment.dto;

import com.kofi.booking_system.appointment.enums.TimeSlot;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CreateAppointmentRequest {

    @NotNull
    private Long providerId;

    @NotNull
    @FutureOrPresent
    private LocalDate appointmentDate;

    @NotNull
    private TimeSlot timeSlot;

}
