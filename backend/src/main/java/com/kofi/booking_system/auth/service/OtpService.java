package com.kofi.booking_system.auth.service;

import com.kofi.booking_system.user.model.Token;
import com.kofi.booking_system.user.model.User;
import com.kofi.booking_system.user.repository.TokenRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    public void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        // TODO SEND EMAIL
        emailService.sendOtpEmail(user.getEmail(),newToken);
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
