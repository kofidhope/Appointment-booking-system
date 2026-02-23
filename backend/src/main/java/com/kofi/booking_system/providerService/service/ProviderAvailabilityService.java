package com.kofi.booking_system.providerService.service;

import com.kofi.booking_system.common.exception.BadRequestException;
import com.kofi.booking_system.common.exception.BookingConflictException;
import com.kofi.booking_system.common.exception.ForbiddenActionException;
import com.kofi.booking_system.common.exception.ResourceNotFoundException;
import com.kofi.booking_system.providerService.dto.CreateAvailabilityResponse;
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

    public CreateAvailabilityResponse createAvailability(CreateAvailabilityRequest request, String providerEmail){
        User provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with email: " + providerEmail));
        if (provider.getRole() != Role.SERVICE_PROVIDER){
            throw new ForbiddenActionException("Only Service Provider can set availability");
        }
        // prevent duplicate day availability
        if (availabilityRepository.existsByProviderAndDayOfWeek(provider, request.getDayOfWeek())){
            throw new BookingConflictException("Availability already exists for this day");
        }

        //validate time range
        if (request.getStartTime().isAfter(request.getEndTime())){
            throw new BadRequestException("Invalid time range");
        }

        ProviderAvailability availability = ProviderAvailability.builder()
                .provider(provider)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
        ProviderAvailability saved = availabilityRepository.save(availability);
        return new  CreateAvailabilityResponse(
                saved.getDayOfWeek(),
                saved.getStartTime(),
                saved.getEndTime()
        );
    }

}
