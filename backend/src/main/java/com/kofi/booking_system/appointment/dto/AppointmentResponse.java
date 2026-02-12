package com.kofi.booking_system.appointment.dto;

import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import com.kofi.booking_system.appointment.enums.TimeSlot;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AppointmentResponse {
    public Long id;
    public LocalDate date;
    public TimeSlot timeSlot;
    public AppointmentStatus status;
}
