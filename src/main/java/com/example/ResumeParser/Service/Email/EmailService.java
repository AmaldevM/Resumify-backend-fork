package com.example.ResumeParser.Service.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class EmailService {
 @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("🔐  Verify Your Email Address");

        String body = "Hello,\n\n"
                + "We received a request to verify your email address.\n\n"
                + "🔐 Your 6-digit verification code is: " + code + "\n\n"
                + "If you didn’t request this, please ignore this email.\n\n"
                + "Thanks & Regards,\n"
                + "Zitra App Team";

        message.setText(body);
        message.setFrom("zitra.pvtltd@gmail.com"); // Optional: depends on your SMTP provider

        mailSender.send(message);
    }
}