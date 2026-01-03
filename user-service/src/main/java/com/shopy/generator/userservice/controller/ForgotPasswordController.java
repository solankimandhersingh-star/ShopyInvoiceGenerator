package com.shopy.generator.userservice.controller;

import com.shopy.generator.userservice.entity.ForgotOtp;
import com.shopy.generator.userservice.repository.ForgotOtpRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;


@Controller
@RequiredArgsConstructor
public class ForgotPasswordController {
    private final ForgotOtpRepository otpRepository;

    @PostMapping("/forgot-password/verify")
    public String verifyOtp(
            @RequestParam String otp,
            HttpSession session,
            Model model) {

        String username = (String) session.getAttribute("FP_USER");

        ForgotOtp record = otpRepository.findByUsername(username)
                .orElse(null);

        if (record == null) {
            model.addAttribute("error", "OTP expired. Please try again.");
            return "forgot-password-otp";
        }

        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpRepository.delete(record);
            model.addAttribute("error", "OTP expired. Request a new one.");
            return "forgot-password-otp";
        }

        if (!record.getOtp().equals(otp)) {
            model.addAttribute("error", "Incorrect OTP");
            return "forgot-password-otp";
        }

        session.setAttribute("FP_VERIFIED", true);

        return "redirect:/user/reset-password";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestParam String username,
            @RequestParam String otp,
            Model model) {

        ForgotOtp stored = otpRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (!stored.getOtp().equals(otp) ||
                stored.getExpiresAt().isBefore(LocalDateTime.now())) {

            model.addAttribute("error", "Invalid / expired OTP");
            model.addAttribute("username", username);
            return "verify-otp";
        }

        model.addAttribute("username", username);

        return "reset-password";
    }
}
