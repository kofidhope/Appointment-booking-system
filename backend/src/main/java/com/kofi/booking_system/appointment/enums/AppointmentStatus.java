package com.kofi.booking_system.appointment.enums;

public enum AppointmentStatus {
    PENDING,     // Customer requested, provider hasn’t confirmed
    CONFIRMED,   // Provider accepted
    REJECTED,
    CANCELLED,    // Cancelled by customer or provider
    EXPIRED
}
