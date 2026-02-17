package com.kofi.booking_system.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyReportResponse {

    private LocalDate date;

    private long totalBookings;
    private long confirmedBookings;
    private long cancelledBookings;
    private long rejectedBookings;

    //  Revenue placeholder (ready for Stripe later)
    private double totalRevenue;

}
