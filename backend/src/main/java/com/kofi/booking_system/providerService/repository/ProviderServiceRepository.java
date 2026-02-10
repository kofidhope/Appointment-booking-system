package com.kofi.booking_system.providerService.repository;

import com.kofi.booking_system.auth.model.User;
import com.kofi.booking_system.providerService.model.ProviderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProviderServiceRepository extends JpaRepository<ProviderService,Long> {

    Page<ProviderService> findByProvider(User provider, Pageable pageable);
}
