package com.kofi.booking_system.admin.service;


import com.kofi.booking_system.admin.dto.DailyReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AdminReportService {

    DailyReportResponse getDailyReport(LocalDate date);

    List<DailyReportResponse> getRangeReport(LocalDate from, LocalDate to);

}
