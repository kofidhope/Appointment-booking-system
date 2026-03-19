package com.kofi.booking_system.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackWebhookData {
    private String reference;  // "PAY12"
    private String status;     // "success"
}