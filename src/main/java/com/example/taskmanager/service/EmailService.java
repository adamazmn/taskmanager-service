package com.example.taskmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendRegistrationEmail(String to, String name) {
        log.info("Sending registration email to {}", to);

        log.info("SMTP USER (env) = {}", System.getenv("SMTP_USERNAME"));
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Welcome to Task Manager!");
            message.setText("Hi " + name + ",\n\n" +
                    "Welcome to our Task Manager app! Your account has been successfully created.\n\n" +
                    "Best regards,\n" +
                    "The Task Manager Team");
            
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
