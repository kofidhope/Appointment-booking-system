package com.kofi.booking_system.providerService.controller;

import com.kofi.booking_system.admin.dto.CreateProviderRequest;
import com.kofi.booking_system.providerService.dto.CreateServiceRequest;
import com.kofi.booking_system.providerService.dto.ProviderServiceResponse;
import com.kofi.booking_system.providerService.model.ProviderService;
import com.kofi.booking_system.providerService.service.ProviderServiceService;
import com.kofi.booking_system.user.model.Role;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/providers")
public class ProviderServiceController {

    private final ProviderServiceService providerService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    @PostMapping("/create/providers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createProvider(@RequestBody @Valid CreateProviderRequest request) {

        User provider = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SERVICE_PROVIDER)
                .enabled(true) // providers don't need OTP verification
                .build();

        userRepository.save(provider);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
