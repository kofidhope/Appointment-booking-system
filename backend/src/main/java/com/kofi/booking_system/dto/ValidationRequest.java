package com.kofi.booking_system.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationRequest {
    private String email;
    private String otp;
}
