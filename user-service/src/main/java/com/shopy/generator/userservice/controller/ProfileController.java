package com.shopy.generator.userservice.controller;

import com.shopy.generator.userservice.dto.ProfileDto;
import com.shopy.generator.userservice.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {

        Long userId = (Long) session.getAttribute("USER");

        ProfileDto dto = service.getProfile(userId);

        model.addAttribute("profile", dto);

        return "user/profile";
    }


    @PostMapping("/save/profile")
    public String saveProfile(@ModelAttribute("profile") ProfileDto dto,
                              HttpSession session) {

        Long userId = (Long) session.getAttribute("USER");

        service.updateProfile(userId, dto);

        return "redirect:/user/profile?success";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute ProfileDto dto, HttpSession session) {

        Long userId = (Long) session.getAttribute("USER");

        service.updateProfile(userId, dto);

        return "redirect:/user/profile?updated=true";
    }

}
