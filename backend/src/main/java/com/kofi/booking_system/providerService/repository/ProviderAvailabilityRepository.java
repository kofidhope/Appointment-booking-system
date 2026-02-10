package com.kofi.booking_system.providerService.repository;

import com.kofi.booking_system.auth.model.User;
import com.kofi.booking_system.providerService.model.ProviderAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, Long> {

    //get availability for a provider
    List<ProviderAvailability> findByProvider(User provider);

    //prevent duplicate availability entries
    boolean existsByProviderAndDayOfWeek(User provider, DayOfWeek dayOfWeek);
}
