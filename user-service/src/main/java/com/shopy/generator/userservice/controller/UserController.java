package com.shopy.generator.userservice.controller;

import com.shopy.generator.userservice.entity.ForgotOtp;
import com.shopy.generator.userservice.entity.UserAccount;
import com.shopy.generator.userservice.repository.ForgotOtpRepository;
import com.shopy.generator.userservice.service.CaptchaService;
import com.shopy.generator.userservice.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final CaptchaService captchaService;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final ForgotOtpRepository otpRepository;

    // ---------------- LOGIN ----------------
    @GetMapping("/login")
    public String login(Model model, HttpSession session) {

        String captcha = captchaService.generate();
        session.setAttribute("CAPTCHA", captcha);

        model.addAttribute("captchaText", captcha);
        System.out.println("loading data");
        return "user-login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String captcha,
            HttpSession session,
            Model model) {

        String expected = (String) session.getAttribute("CAPTCHA");


        if (expected == null || !expected.equalsIgnoreCase(captcha)) {
            model.addAttribute("error", "Invalid captcha");
            return "user-login";
        }


        try {
            UserAccount user = userService.authenticate(username, password);
            session.setAttribute("USER", user.getId());
            if (user.isFirstLogin()) {
                return "redirect:/user/change-password";
            }

            return "redirect:/user/home";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user-login";
        }
    }

    // ---------------- CHANGE PASSWORD ----------------
    @GetMapping("/change-password")
    public String changePasswordPage() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String newPassword,
            HttpSession session,
            Model model
    ) {

        Long userId = (Long) session.getAttribute("USER");

        if (userId == null) {
            return "redirect:/user/login";
        }

        userService.changePassword(userId, newPassword);

        session.invalidate();

        return "redirect:/user/login";
    }

    // ---------------- FORGOT PASSWORD ----------------
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendOtp(
            @RequestParam String value,
            Model model) {

        // validate user exists
        UserAccount user = userService.findByUsernameOrMobile(value);

        // call notification-service
        String otp = restTemplate.postForObject(
                "http://localhost:8086/notify/otp?user=" + value,
                null,
                String.class
        );

        // save OTP
        otpRepository.save(
                ForgotOtp.builder()
                        .username(value)
                        .otp(otp)
                        .expiresAt(LocalDateTime.now().plusMinutes(5))
                        .build()
        );

        model.addAttribute("username", value);

        return "verify-otp";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String username,
            @RequestParam String password) {

        userService.updatePassword(username, password);

        otpRepository.deleteByUsername(username);

        return "forgot-password-success";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }




}
