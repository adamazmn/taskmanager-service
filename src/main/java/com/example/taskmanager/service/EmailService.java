package com.example.taskmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
// @Service  // Disabled - using ResendEmailService instead
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${from.email}")
    private String fromEmail;

    @Async
    public void sendRegistrationEmail(String to, String name) {
        log.info("Sending registration email to {}", to);

        // Skip if email is not configured
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("Email not configured (SMTP_USERNAME not set). Skipping email send.");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Welcome to MYDAILY Task Manager!");
            message.setText("Hi " + name + ",\n\n" +
                    "Welcome to MYDAILY Task Manager! Your account has been successfully created.\n\n" +
                    "You can now log in and start managing your tasks.\n\n" +
                    "Best regards,\n" +
                    "The MYDAILY Team");
            
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }
}
