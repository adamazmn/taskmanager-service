package com.example.taskmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ResendEmailService {

    @Value("${resend.api.key:}")
    private String apiKey;

    @Value("${resend.from.email:onboarding@resend.dev}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    @Async
    public void sendRegistrationEmail(String to, String name) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("Resend API key not configured. Skipping email send.");
            return;
        }

        log.info("Sending registration email to {} via Resend", to);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("from", "MYDAILY Task Manager <" + fromEmail + ">");
            body.put("to", new String[]{to});
            body.put("subject", "Welcome to MYDAILY Task Manager!");
            body.put("html", buildWelcomeEmailHtml(name));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    RESEND_API_URL,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully to {} via Resend", to);
            } else {
                log.error("Failed to send email via Resend: {}", response.getBody());
            }

        } catch (Exception e) {
            log.error("Error sending email via Resend to {}: {}", to, e.getMessage());
        }
    }

    private String buildWelcomeEmailHtml(String name) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #1a1a2e 0%%, #16213e 100%%); padding: 40px; text-align: center; }
                    .header h1 { color: white; margin: 0; font-size: 28px; }
                    .content { padding: 40px; }
                    .content h2 { color: #1a1a2e; margin-top: 0; }
                    .content p { color: #666; line-height: 1.6; }
                    .button { display: inline-block; background: #1a1a2e; color: white; padding: 14px 28px; text-decoration: none; border-radius: 8px; margin-top: 20px; }
                    .footer { background: #f9f9f9; padding: 20px; text-align: center; color: #999; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚀 MYDAILY</h1>
                    </div>
                    <div class="content">
                        <h2>Welcome aboard, %s!</h2>
                        <p>Your account has been successfully created. You're now ready to take control of your daily tasks with precision and clarity.</p>
                        <p>Start organizing your life today:</p>
                        <ul style="color: #666; line-height: 2;">
                            <li>✅ Create and manage tasks effortlessly</li>
                            <li>📅 Set due dates and stay on track</li>
                            <li>📎 Attach files to your tasks</li>
                            <li>📊 Track your productivity</li>
                        </ul>
                        <p>Ready to get started?</p>
                    </div>
                    <div class="footer">
                        <p>© 2024 MYDAILY Task Manager. All rights reserved.</p>
                        <p>This email was sent because you created an account on MYDAILY.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(name);
    }
}
