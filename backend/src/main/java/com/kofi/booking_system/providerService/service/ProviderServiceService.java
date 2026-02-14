package com.kofi.booking_system.providerService.service;

import com.kofi.booking_system.common.exception.ForbiddenActionException;
import com.kofi.booking_system.common.exception.ResourceNotFoundException;
import com.kofi.booking_system.user.model.Role;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.UserRepository;
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

@Service
@RequiredArgsConstructor
public class ProviderServiceService {

    private final ProviderServiceRepository repository;
    private final UserRepository userRepository;

    public ProviderService createService(CreateServiceRequest request, String providerEmail){
        //1. fetch the user
        User provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with email: " + providerEmail));
        // check if user has a provider as role
        if(provider.getRole() != Role.SERVICE_PROVIDER){
            throw new ForbiddenActionException("User is not a service provider");
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
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id: " + providerId));

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
