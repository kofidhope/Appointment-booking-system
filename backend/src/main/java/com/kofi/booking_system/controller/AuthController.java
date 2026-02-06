package com.kofi.booking_system.controller;

import com.kofi.booking_system.dto.AuthResponse;
import com.kofi.booking_system.dto.LoginRequest;
import com.kofi.booking_system.dto.RegisterRequest;
import com.kofi.booking_system.dto.VerifyOtpRequest;
import com.kofi.booking_system.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) throws MessagingException {
        authService.register(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("User Registered successfully. Check your mail for OTP");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request){
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request){
        authService.verifyOtp(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("OTP Verified successfully");
    }
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestBody VerifyOtpRequest request) throws MessagingException {
        authService.resendOtp(request);
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body("OTP Resend successfully");
    }

}
