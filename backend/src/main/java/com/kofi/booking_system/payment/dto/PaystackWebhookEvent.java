package com.kofi.booking_system.payment.dto;

import lombok.Data;

@Data
public class PaystackWebhookEvent {
    private String event;        // e.g. "charge.success"
    private PaystackWebhookData data;
}

