package com.kofi.booking_system.service;

import com.kofi.appointmentbookingsystem.exception.InvalidCredentialsException;
import com.kofi.appointmentbookingsystem.exception.ResourceAlreadyExistsException;
import com.kofi.booking_system.dto.AuthResponse;
import com.kofi.booking_system.dto.LoginRequest;
import com.kofi.booking_system.dto.RegisterRequest;
import com.kofi.booking_system.dto.VerifyOtpRequest;
import com.kofi.booking_system.model.Role;
import com.kofi.booking_system.model.Token;
import com.kofi.booking_system.model.User;
import com.kofi.booking_system.repository.TokenRepository;
import com.kofi.booking_system.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    public void register(RegisterRequest request) throws MessagingException {
        //1.check if the email exist
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already in use");
        }
        //.create the user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(false)
                .build();
        //3.save the user
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
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

    public void verifyOtp(VerifyOtpRequest request){
        //1. fetch user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InvalidCredentialsException("User not found"));
        //2. fetch otp
        Token otp = tokenRepository.findByToken(request.getOtp())
                .orElseThrow(()-> new InvalidCredentialsException("Otp not found"));
        //3. ensure the otp belongs to this user
        if (!otp.getUser().getId().equals(user.getId())) {
            throw new InvalidCredentialsException("Invalid OTP");
        }
        //4. check for expiry
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialsException("Otp is expired");
        }
        //5. prevent re-verification
        if(otp.getValidatedAt()!=null) {
            throw new InvalidCredentialsException("OTP already used");
        }
        //6. enable and save user
        user.setEnabled(true);
        userRepository.save(user);
        //7. Mark otp as validated
        otp.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(otp);
    }

    public void resendOtp(VerifyOtpRequest request) throws MessagingException {
        //1. fetch user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InvalidCredentialsException("User not found"));
        //2. check if the user is already enables
        if (user.isEnabled()){
            throw new InvalidCredentialsException("Account Already verified.");
        }
        //3. Invalidate old OTP if exist
        tokenRepository.findByUserAndValidatedAtIsNull(user)
                .ifPresent(token -> {
                    token.setExpiresAt(LocalDateTime.now());
                    tokenRepository.save(token);
                });
        //4. generate and send token to email
        sendValidationEmail(user);
    }

    public AuthResponse login(LoginRequest request){
        //1. find the email if exist or throw and error
        User user = (userRepository.findByEmail(request.getEmail()))
                .orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));
        // check if the account is verified
        if (!user.isEnabled()) {
            throw new InvalidCredentialsException("Account not verified. Please verify your email.");
        }
        // check if the password matches
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }
        // 3. generate JWT token with subject and role
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        //4. build a response object
        AuthResponse response = new AuthResponse();
        response.setUserId(user.getId());
        response.setToken(token);
        return response;
    }

}
