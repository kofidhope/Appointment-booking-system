package com.kofi.booking_system.providerService.controller;

import com.kofi.booking_system.providerService.dto.CreateAvailabilityRequest;
import com.kofi.booking_system.providerService.dto.CreateAvailabilityResponse;
import com.kofi.booking_system.providerService.service.ProviderAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/provider/availability")
@RequiredArgsConstructor
public class ProviderAvailabilityController {

    private final ProviderAvailabilityService service;

    @PostMapping
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<CreateAvailabilityResponse> createAvailability(
            @RequestBody CreateAvailabilityRequest request,
            Authentication authentication
    ){
        String email = authentication.getName();
        return ResponseEntity.ok(service.createAvailability(request,email));
    }


}
