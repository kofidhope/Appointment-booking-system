package com.kofi.booking_system.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AuthResponse {
    private Long userId;
    private String token;
    private String refreshToken;
}
