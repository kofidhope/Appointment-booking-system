package com.kofi.booking_system.providerService.repository;

import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.providerService.model.ProviderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderServiceRepository extends JpaRepository<ProviderService,Long> {

    Page<ProviderService> findByProvider(User provider, Pageable pageable);
}
