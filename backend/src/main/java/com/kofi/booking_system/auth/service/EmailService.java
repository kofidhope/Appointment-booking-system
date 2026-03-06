package com.kofi.booking_system.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {

        try {
            System.out.println("Sending OTP email to: " + toEmail);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED,"UTF-8");

            //Thymeleaf context
            Context context = new Context();
            context.setVariable("otp", otp);
            //process the html template
            String htmlContent = templateEngine.process("email_template", context);
            System.out.println("Template processed successfully");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Verify your account");
            helper.setText(htmlContent,true);

            System.out.println("About to send email...");

            mailSender.send(message);
            log.info("Email sent successfully to {}", toEmail);
        }  catch (Exception e) {
            log.error("Email sending failed: {}", e.getMessage(), e); // THIS WILL SHOW THE REAL ERROR
            throw new MessagingException(e.getMessage(), e);
        }
    }


}
