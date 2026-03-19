package com.kofi.booking_system.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackWebhookEvent {
    private String event;        // e.g. "charge.success"
    private PaystackWebhookData data;
}

