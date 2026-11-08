
package com.skillpath.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.mail.from:}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String email, String token) {

        String resetLink = frontendUrl
                + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        if (fromEmail != null && !fromEmail.isBlank()) {
            message.setFrom(fromEmail);
        }

        message.setTo(email);
        message.setSubject("SkillPath - Password Reset");
        message.setText(
                "Hello,\n\n"
                + "We received a request to reset your SkillPath password.\n\n"
                + "Click the link below to reset your password:\n"
                + resetLink + "\n\n"
                + "This link expires in 15 minutes.\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Regards,\nSkillPath Team"
        );

        mailSender.send(message);
    }
}
