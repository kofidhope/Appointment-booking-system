package com.kofi.booking_system.payment.dto;

import lombok.Data;

@Data
public class PaystackInitResponse {
    private boolean status;
    private PaystackInitData data;

    @Data
    public static class PaystackInitData {
        private String authorization_url;
    }
}
