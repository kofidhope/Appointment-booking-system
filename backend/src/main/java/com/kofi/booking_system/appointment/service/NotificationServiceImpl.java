package com.kofi.booking_system.appointment.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String htmlBody) {
      try{
          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"UTF-8");

          helper.setTo(to);
          helper.setSubject(subject);
          helper.setText(htmlBody,true);

          mailSender.send(mimeMessage);
      }catch (MessagingException e) {
          throw new RuntimeException("Failed to send email", e);
      }
    }

    @Override
    public void sendSms(String phone, String message) {
        // TODO SEND SMS NOTIFICATION
    }
}
