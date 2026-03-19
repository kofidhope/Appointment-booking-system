package com.kofi.booking_system.appointment.service;

import com.kofi.booking_system.appointment.model.Appointment;
import com.kofi.booking_system.appointment.repository.AppointmentRepository;
import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentReminderJob {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;
    private final EmailTemplateService emailTemplateService;

    private static final Logger log = LoggerFactory.getLogger(AppointmentReminderJob.class);

    // Runs every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void sendAppointmentReminders() {
        log.info("Running appointment reminder job...");

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Appointment> appointments = appointmentRepository
                .findByAppointmentDateAndStatus(tomorrow, AppointmentStatus.CONFIRMED);

        for (Appointment appointment : appointments) {
            try {
                String html = emailTemplateService.renderAppointmentReminder(appointment);
                notificationService.sendEmail(
                        appointment.getCustomer().getEmail(),
                        "⏰ Appointment Reminder - Tomorrow!",
                        html
                );
                log.info("Reminder sent to: {}", appointment.getCustomer().getEmail());
            } catch (Exception e) {
                log.error("Failed to send reminder to {}: {}",
                        appointment.getCustomer().getEmail(), e.getMessage());
            }
        }

        log.info("Reminder job completed. {} reminders sent.", appointments.size());
    }
}