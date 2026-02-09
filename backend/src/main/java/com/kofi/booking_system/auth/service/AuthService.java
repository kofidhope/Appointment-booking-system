package com.kofi.booking_system.service;

import com.kofi.appointmentbookingsystem.exception.InvalidCredentialsException;
import com.kofi.appointmentbookingsystem.exception.ResourceAlreadyExistsException;
import com.kofi.booking_system.dto.*;
import com.kofi.booking_system.model.RefreshToken;
import com.kofi.booking_system.model.Role;
import com.kofi.booking_system.model.Token;
import com.kofi.booking_system.model.User;
import com.kofi.booking_system.repository.TokenRepository;
import com.kofi.booking_system.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final OtpService otpService;
    private final RefreshTokenService refreshTokenService;

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
       otpService.sendValidationEmail(user);
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

    public void resendOtp(ResendOtpRequest request) throws MessagingException {
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
        otpService.sendValidationEmail(user);
    }

    public void forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        //1. fetch the user if exist
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InvalidCredentialsException("User not found"));
        //2.send OTP to the email
        otpService.sendValidationEmail(user);
    }

    public void resetPassword(ResetPasswordRequest request) throws MessagingException {
        //1.fetch the user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new InvalidCredentialsException("User not found"));
        //2. fetch the token
        Token otp = tokenRepository.findByToken(request.getOtp())
                .orElseThrow(()-> new InvalidCredentialsException("Otp not found"));
        //3. check if hte email belongs to the user
        if (!otp.getUser().getId().equals(user.getId())) {
            throw new InvalidCredentialsException("Invalid OTP");
        }
        //4. check the expiry of the token
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidCredentialsException("Otp is expired");
        }
        //.5 check if otp is not validated
        if (otp.getValidatedAt()!=null) {
            throw new InvalidCredentialsException("OTP already used");
        }
        //6. update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        //7.Mark otp as used
        otp.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(otp);

    }

    public AuthResponse login(LoginRequest request){
        //1. find the email if exist or throw and error
        User user = (userRepository.findByEmail(request.getEmail()))
                .orElseThrow(()-> new InvalidCredentialsException("Invalid email or password"));
        //2. check if the account is verified
        if (!user.isEnabled()) {
            throw new InvalidCredentialsException("Account not verified. Please verify your email.");
        }

        //3. check if account is locked
        if(user.getLockUntil()!=null && user.getLockUntil().isAfter(LocalDateTime.now())){
            throw new InvalidCredentialsException("Account locked. Try again after " + user.getLockUntil());
        }
        //4. auto-unlock if lock expired
        if (user.getLockUntil() != null &&
                user.getLockUntil().isBefore(LocalDateTime.now())) {

            user.setLockUntil(null);
            user.setFailedLoginAttempt(0);
        }
        //5. check if the password matches
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){

            //increment failed attempt
            int attempts = user.getFailedLoginAttempt()+1;
            user.setFailedLoginAttempt(attempts);
            //lock account if threshold is reached
            if (attempts >= 5){
                user.setLockUntil(LocalDateTime.now().plusMinutes(10));
            }
            userRepository.save(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        //6. successful login -> reset counter
        user.setFailedLoginAttempt(0);
        user.setLockUntil(null);
        userRepository.save(user);

        //7. generate JWT token with subject and role
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        //8. Generate refresh token
        RefreshToken refreshToken = refreshTokenService.create(user);
        //9. build a response object
        AuthResponse response = new AuthResponse();
        response.setUserId(user.getId());
        response.setToken(token);
        response.setRefreshToken(refreshToken.getToken());
        return response;
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {

        // 1. Verify old refresh token
        RefreshToken oldToken = refreshTokenService.verify(request.getRefreshToken());

        User user = oldToken.getUser();

        // 2. Revoke old refresh token
        refreshTokenService.revoke(oldToken);

        // 3. Create NEW refresh token
        RefreshToken newRefreshToken = refreshTokenService.create(user);

        // 4. Create new access token
        String newAccessToken =
                jwtService.generateToken(user.getEmail(), user.getRole().name());

        // 5. Respond
        AuthResponse response = new AuthResponse();
        response.setUserId(user.getId());
        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken.getToken());

        return response;
    }


}
