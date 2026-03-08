package com.kofi.booking_system.providerService.service;

import com.kofi.booking_system.common.exception.BadRequestException;
import com.kofi.booking_system.common.exception.BookingConflictException;
import com.kofi.booking_system.common.exception.ForbiddenActionException;
import com.kofi.booking_system.common.exception.ResourceNotFoundException;
import com.kofi.booking_system.providerService.dto.AvailabilityResponse;
import com.kofi.booking_system.user.model.Role;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.UserRepository;
import com.kofi.booking_system.providerService.dto.CreateAvailabilityRequest;
import com.kofi.booking_system.providerService.model.ProviderAvailability;
import com.kofi.booking_system.providerService.repository.ProviderAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderAvailabilityService {

    private final ProviderAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    @Transactional
    public AvailabilityResponse createAvailability(CreateAvailabilityRequest request, String providerEmail){
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
        if (!request.getStartTime().isBefore(request.getEndTime())){
            throw new BadRequestException("Start time must be before end time");
        }

        ProviderAvailability availability = ProviderAvailability.builder()
                .provider(provider)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
        ProviderAvailability saved = availabilityRepository.save(availability);
        return new AvailabilityResponse(
                saved.getId(),
                saved.getDayOfWeek(),
                saved.getStartTime(),
                saved.getEndTime()
        );
    }

    public List<AvailabilityResponse> getAvailabilityByProvider(Long providerId){
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        if (provider.getRole() != Role.SERVICE_PROVIDER){
            throw new ResourceNotFoundException("Provider not found");
        }
        return availabilityRepository.findByProvider(provider)
                .stream()
                .map(a -> new AvailabilityResponse(
                        a.getId(),
                        a.getDayOfWeek(),
                        a.getStartTime(),
                        a.getEndTime()
                ))
                .toList();
    }

    @Transactional
    public void deleteAvailability(Long availabilityId, String email) {
        ProviderAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found"));

        // make sure the logged-in provider owns this availability
        if (!availability.getProvider().getEmail().equals(email)) {
            throw new ForbiddenActionException("Not authorized to delete this availability");
        }

        availabilityRepository.delete(availability);
    }
}
