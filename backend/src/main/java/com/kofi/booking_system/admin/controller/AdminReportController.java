package com.kofi.booking_system.admin.controller;

import com.kofi.booking_system.admin.dto.DailyReportResponse;
import com.kofi.booking_system.admin.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/report")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/daily")
    public DailyReportResponse getDailyReport(@RequestParam LocalDate date){
        return adminReportService.getDailyReport(date);
    }

    @GetMapping("/range")
    public List<DailyReportResponse> getRangeReport(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to){
        return adminReportService.getRangeReport(from, to);
    }

}
