package com.kofi.booking_system.providerService.controller;

import com.kofi.booking_system.providerService.dto.CreateServiceRequest;
import com.kofi.booking_system.providerService.service.ProviderServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/provider/service")
public class ProviderServiceController {

    private final ProviderServiceService serviceProvider;

    @PostMapping
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<?> createService(
            @RequestBody @Valid CreateServiceRequest request, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(serviceProvider.createService(request,email));
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<?> getProviderServices(
            @PathVariable Long providerId
    ) {
        return ResponseEntity.ok(serviceProvider.getServicesByProvider(providerId));
    }

}
