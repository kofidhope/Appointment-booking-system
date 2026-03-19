package com.kofi.booking_system.appointment.service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Value("${TWILIO_PHONE_NUMBER}")
    private String fromNumber;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void sendEmail(String to, String subject, String htmlBody) {
        log.info("Attempting to send email to: {}", to);
      try{
          MimeMessage mimeMessage = mailSender.createMimeMessage();
          MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true,"UTF-8");

          helper.setFrom(fromEmail);
          helper.setTo(to);
          helper.setSubject(subject);
          helper.setText(htmlBody,true);

          mailSender.send(mimeMessage);
          log.info("Email sent successfully to: {}", to);
      }catch (MessagingException e) {
          log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
          throw new RuntimeException("Failed to send email", e);
      }
    }

    @Override
    public void sendSms(String to, String message) {

        try {
            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromNumber),
                    message
            ).create();
            log.info("SMS sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage(), e);
        }
    }
}
