package com.kofi.booking_system.appointment.service;

public interface AppointmentCleanupService {


    /**
     * Expires all PENDING appointments older than X minutes
     */
    void expireOldPendingBookings();

}
