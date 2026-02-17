package com.kofi.booking_system.appointment.service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
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
    public void sendSms(String to, String message) {

            Message.creator(
                    new PhoneNumber(to),     // recipient number
                    new PhoneNumber(fromNumber), // Twilio number
                    message                  // SMS body
            ).create();
    }
}
