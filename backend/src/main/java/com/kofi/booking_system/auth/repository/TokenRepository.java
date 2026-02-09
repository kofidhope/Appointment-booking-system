package com.kofi.booking_system.auth.repository;

import com.kofi.booking_system.auth.model.Token;
import com.kofi.booking_system.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {

    Optional<Token> findByToken(String token);
    Optional<Token> findByUserAndValidatedAtIsNull(User user);

}
