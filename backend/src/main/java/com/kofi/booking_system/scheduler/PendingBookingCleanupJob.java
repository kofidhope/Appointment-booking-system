package com.kofi.booking_system.scheduler;

import com.kofi.booking_system.appointment.service.AppointmentCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingBookingCleanupJob {

    private final AppointmentCleanupService cleanupService;

    //Runs every 5 mins

    @Scheduled(fixedRate = 5*60*1000)
    public void cleanExpiredPendingBookings() {

        log.info("Running PENDING booking cleanup job...");

        cleanupService.expireOldPendingBookings();

        log.info("Cleanup job completed.");
    }

}
