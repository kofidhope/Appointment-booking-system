package com.kofi.booking_system.providerService.service;

import com.kofi.booking_system.auth.model.Role;
import com.kofi.booking_system.auth.model.User;
import com.kofi.booking_system.auth.repository.UserRepository;
import com.kofi.booking_system.providerService.dto.CreateServiceRequest;
import com.kofi.booking_system.providerService.dto.ProviderServiceResponse;
import com.kofi.booking_system.providerService.model.ProviderService;
import com.kofi.booking_system.providerService.repository.ProviderServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderServiceService {

    private final ProviderServiceRepository repository;
    private final UserRepository userRepository;

    public ProviderService createService(CreateServiceRequest request, String providerEmail){
        //1. fetch the user
        User provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(()-> new RuntimeException("User not found with email: " + providerEmail));
        // check if user has a provider as role
        if(provider.getRole() != Role.SERVICE_PROVIDER){
            throw new RuntimeException("User is not a service provider");
        }
        ProviderService serviceProvider = ProviderService.builder()
                .provider(provider)
                .name(request.getName())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .price(request.getPrice())
                .build();
        return repository.save(serviceProvider);

    }

    public Page<ProviderServiceResponse> getServicesByProvider(Long providerId,int page,int size){
        User provider = userRepository.findById(providerId)
                .orElseThrow(()-> new RuntimeException("User not found with id: " + providerId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return repository.findByProvider(provider,pageable)
                .map(s -> new ProviderServiceResponse(
                        s.getId(),
                        s.getName(),
                        s.getDescription(),
                        s.getDurationMinutes(),
                        s.getPrice()
                ));
    }

}
