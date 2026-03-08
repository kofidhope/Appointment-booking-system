package com.kofi.booking_system.appointment.service;

import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import com.kofi.booking_system.appointment.model.Appointment;
import com.kofi.booking_system.appointment.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentCleanupServiceImpl implements AppointmentCleanupService {

    private final AppointmentRepository appointmentRepository;
    private final EmailTemplateService emailTemplateService;
    private final NotificationService notificationService;

    private static final Logger log = LoggerFactory.getLogger(AppointmentCleanupServiceImpl.class);

    @Override
    public void expireOldPendingBookings() {
        LocalDateTime expiredTime = LocalDateTime.now().minusDays(60);

        int page = 0;
        int size = 100;

        Slice<Appointment> slice;
        do {
            slice = appointmentRepository.findByStatusAndCreatedAtBefore(
                    AppointmentStatus.PENDING,
                    expiredTime,
                    PageRequest.of(page, size, Sort.by("id").ascending())
            );

            for (Appointment appointment : slice.getContent()) {
                expireAndNotify(appointment);
            }
            page++;
        } while (slice.hasNext());
    }

    private void expireAndNotify(Appointment appointment) {
        appointment.setStatus(AppointmentStatus.EXPIRED);
        appointment.setExpiredAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
        try {
            String html = emailTemplateService.renderAppointmentExpired(appointment);
            notificationService.sendEmail(
                    appointment.getCustomer().getEmail(),
                    "⏰ Your Appointment Has Expired",
                    html
            );
        }catch (Exception e) {
            log.warn("Failed to send expiry email for appointment {}: {}", appointment.getId(), e.getMessage());
        }

    }
}
