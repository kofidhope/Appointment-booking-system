package com.kofi.booking_system.payment.dto;

import lombok.Data;

@Data
public class PaystackVerifyResponse {
    private boolean status;
    private PaystackVerifyData data;

    @Data
    public static class PaystackVerifyData {
        private String status; // success | failed
    }
}