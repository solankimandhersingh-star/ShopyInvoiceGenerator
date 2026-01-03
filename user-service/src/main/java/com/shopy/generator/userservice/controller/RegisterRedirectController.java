package com.shopy.generator.userservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class RegisterRedirectController {

    @GetMapping("/register")
    public String redirectToRegister() {
        return "redirect:/portal/register";
    }
}

