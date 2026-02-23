package com.kofi.booking_system.config;

import com.kofi.booking_system.user.model.Role;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminBootstrap {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner createAdmin() {
        return args -> {
            if (!userRepository.existsByEmail("admin@booking.com")) {

                User admin = User.builder()
                        .firstName("System")
                        .lastName("Admin")
                        .email("admin@booking.com")
                        .phoneNumber("+233248603202")
                        .password(passwordEncoder.encode("!Admin@1234"))
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build();

                userRepository.save(admin);
            }
        };
    }
}