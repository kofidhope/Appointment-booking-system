package com.kofi.booking_system.providerService.service;

import com.kofi.booking_system.user.model.Role;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.UserRepository;
import com.kofi.booking_system.providerService.dto.CreateAvailabilityRequest;
import com.kofi.booking_system.providerService.model.ProviderAvailability;
import com.kofi.booking_system.providerService.repository.ProviderAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProviderAvailabilityService {

    private final ProviderAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public ProviderAvailability createAvailability(CreateAvailabilityRequest request, String providerEmail){
        User provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(()-> new RuntimeException("user not found"));
        if (provider.getRole() != Role.SERVICE_PROVIDER){
            throw new RuntimeException("Only Service Provider can set availability");
        }
        // prevent duplicate day availability
        if (availabilityRepository.existsByProviderAndDayOfWeek(provider, request.getDayOfWeek())){
            throw new RuntimeException("Availability already exists for this day");
        }

        //validate time range
        if (request.getStartTime().isAfter(request.getEndTime())){
            throw new RuntimeException("Invalid time range");
        }

        ProviderAvailability availability = ProviderAvailability.builder()
                .provider(provider)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
        return availabilityRepository.save(availability);
    }

}
