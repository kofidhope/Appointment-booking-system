package com.kofi.booking_system.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
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

    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED,"UTF-8");

        //Thymeleaf context
        Context context = new Context();
        context.setVariable("otp", otp);
        //process the html template
        String htmlContent = templateEngine.process("email_template", context);

        helper.setTo(toEmail);
        helper.setSubject("Verify your account");
        helper.setText(htmlContent,true);

        mailSender.send(message);
    }


}
