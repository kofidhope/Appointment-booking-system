package com.kofi.booking_system.repository;

import com.kofi.booking_system.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {

    Optional<Token> findByTokenAndValidatedAtIsNull(String token);
}
