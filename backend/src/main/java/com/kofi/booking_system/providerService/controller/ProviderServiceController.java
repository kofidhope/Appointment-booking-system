package com.kofi.booking_system.providerService.controller;

import com.kofi.booking_system.providerService.dto.CreateServiceRequest;
import com.kofi.booking_system.providerService.dto.ProviderServiceResponse;
import com.kofi.booking_system.providerService.model.ProviderService;
import com.kofi.booking_system.providerService.service.ProviderServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/providers")
public class ProviderServiceController {

    private final ProviderServiceService providerService;

    @PostMapping("/services")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<ProviderService> createService(
            @RequestBody @Valid CreateServiceRequest request, Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(
                providerService.createService(request,email)
        );
    }

    @GetMapping("/{providerId}/services")
    public ResponseEntity<Page<ProviderServiceResponse>> getProviderServices(
            @PathVariable Long providerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(providerService.getServicesByProvider(providerId,page,size));
    }

}
