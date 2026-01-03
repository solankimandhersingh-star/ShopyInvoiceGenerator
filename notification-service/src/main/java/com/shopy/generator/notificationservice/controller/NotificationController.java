package com.shopy.generator.notificationservice.controller;

import com.shopy.generator.notificationservice.dto.EmailRequest;
import com.shopy.generator.notificationservice.dto.InvoiceRequest;
import com.shopy.generator.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping("/email")
    public void sendEmail(@RequestBody EmailRequest req) {
        service.sendEmail(req.to(), req.subject(), req.body());
    }

    @PostMapping("/otp")
    public String sendOtp(@RequestParam String user) {
        return service.sendOtp(user);
    }

    @PostMapping("/invoice")
    public void invoice(@RequestBody InvoiceRequest req) {
        service.sendInvoiceNotification(req.email(), req.party(), req.amount());
    }
}

