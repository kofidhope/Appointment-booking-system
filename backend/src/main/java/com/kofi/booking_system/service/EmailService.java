package com.kofi.booking_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Account Verification – Booking System");

        message.setText("""
            Hello,

            Welcome to Booking System

            To activate your account, please use the verification code below:

            Verification Code: %s

            This code will expire in 10 minutes.
            If you did not create this account, please ignore this email.

            Best regards,
            Booking System Team
            """.formatted(otp));

        mailSender.send(message);
    }


}
