package com.kofi.booking_system.user.repository;

import com.kofi.booking_system.user.model.Role;
import com.kofi.booking_system.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRole(Role role);
}
