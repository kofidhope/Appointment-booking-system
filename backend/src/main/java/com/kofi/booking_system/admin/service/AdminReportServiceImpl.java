package com.kofi.booking_system.admin.service;

import com.kofi.booking_system.admin.dto.DailyReportResponse;
import com.kofi.booking_system.appointment.enums.AppointmentStatus;
import com.kofi.booking_system.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final AppointmentRepository appointmentRepository;

    @Override
    public DailyReportResponse getDailyReport(LocalDate date) {
        long total = appointmentRepository.countByAppointmentDate(date);
        long confirmed = appointmentRepository.countByAppointmentDateAndStatus(date, AppointmentStatus.CONFIRMED);
        long cancelled = appointmentRepository.countByAppointmentDateAndStatus(date, AppointmentStatus.CANCELLED);
        long rejected = appointmentRepository.countByAppointmentDateAndStatus(date, AppointmentStatus.REJECTED);

        // Placeholder for payments later
        double revenue = 0.0;

        return new DailyReportResponse(
                date,
                total,
                confirmed,
                cancelled,
                rejected,
                revenue
        );
    }

    @Override
    public List<DailyReportResponse> getRangeReport(LocalDate from, LocalDate to) {
            List<DailyReportResponse> reports = new ArrayList<>();
            LocalDate current = from;

            while (!current.isAfter(to)) {
                reports.add(getDailyReport(current));
                current = current.plusDays(1);
            }

            return reports;
    }
}
