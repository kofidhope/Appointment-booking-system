package com.kofi.booking_system.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResendOtpRequest {
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
}
