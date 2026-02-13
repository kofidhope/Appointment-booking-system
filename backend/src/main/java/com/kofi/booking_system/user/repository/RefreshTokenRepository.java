package com.kofi.booking_system.user.repository;

import com.kofi.booking_system.user.model.RefreshToken;
import com.kofi.booking_system.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
