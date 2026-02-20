package com.kofi.booking_system.payment.enums;

public enum PaymentStatus {
    PENDING,     // created but not yet paid
    SUCCESS,     // paid successfully
    FAILED,      // payment failed
    CANCELLED,    // user cancelled
    REFUNDED
}
