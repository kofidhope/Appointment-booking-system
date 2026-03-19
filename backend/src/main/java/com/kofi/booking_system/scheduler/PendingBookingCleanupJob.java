package com.kofi.booking_system.scheduler;

import com.kofi.booking_system.appointment.service.AppointmentCleanupService;
import com.kofi.booking_system.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingBookingCleanupJob {

    private final AppointmentCleanupService cleanupService;
    private final RefreshTokenRepository refreshTokenRepository;

    // Runs every 5 mins
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void cleanExpiredPendingBookings() {
        log.info("Running PENDING booking cleanup job...");
        cleanupService.expireOldPendingBookings();
        log.info("Cleanup job completed.");
    }

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanRevokedRefreshTokens() {
        log.info("Running revoked refresh token cleanup job...");
        refreshTokenRepository.deleteByRevokedAtIsNotNull();
        log.info("Revoked refresh token cleanup completed.");
    }
}
