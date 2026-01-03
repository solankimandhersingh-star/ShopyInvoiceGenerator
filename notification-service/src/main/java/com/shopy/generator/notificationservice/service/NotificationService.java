package com.shopy.generator.notificationservice.service;

import com.shopy.generator.notificationservice.entity.NotificationLog;
import com.shopy.generator.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository repo;


    public void sendOtp(String mobile, String otp) {
        System.out.println("📩 SMS SENT TO " + mobile + " → OTP: " + otp);
    }

    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);

        mailSender.send(msg);

        repo.save(NotificationLog.builder()
                .type("EMAIL")
                .recipient(to)
                .message(subject)
                .sentAt(LocalDateTime.now())
                .build());
    }

    public String sendOtp(String mobileOrEmail) {

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        repo.save(NotificationLog.builder()
                .type("OTP")
                .recipient(mobileOrEmail)
                .message("OTP: " + otp)
                .sentAt(LocalDateTime.now())
                .build());

        System.out.println("OTP SENT: " + otp);

        return otp;
    }

    public void sendInvoiceNotification(String email, String party, double amount) {

        String text = "Invoice created for " + party +
                " with amount ₹" + amount + " on " + LocalDateTime.now();

        sendEmail(email, "Invoice Created", text);
    }
}

