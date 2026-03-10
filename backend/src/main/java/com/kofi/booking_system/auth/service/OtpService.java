package com.kofi.booking_system.auth.service;

import com.kofi.booking_system.user.model.Token;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    @Async
    public void sendValidationEmail(User user){
        try {
            var newToken = generateAndSaveActivationToken(user);
            emailService.sendOtpEmail(user.getEmail(), newToken);
        } catch (Exception e) {
            log.error("Failed to send validation email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    private String generateAndSaveActivationToken(User user) {
        String generateToken = generateCode(6);
        var token = Token.builder()
                .token(generateToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generateToken;
    }

    private String generateCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

}
