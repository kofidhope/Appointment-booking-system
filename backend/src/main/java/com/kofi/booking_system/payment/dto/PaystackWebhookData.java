package com.kofi.booking_system.payment.dto;

import lombok.Data;

@Data
public class PaystackWebhookData {
    private String reference;  // "PAY12"
    private String status;     // "success"
}