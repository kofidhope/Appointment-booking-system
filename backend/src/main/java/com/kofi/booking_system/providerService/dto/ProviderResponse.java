package com.kofi.booking_system.providerService.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProviderResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private List<ProviderServiceResponse> services;
    private List<AvailabilityResponse> availability;
}