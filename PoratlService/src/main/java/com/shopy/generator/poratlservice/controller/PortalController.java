package com.shopy.generator.poratlservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortalController {

    @GetMapping("/portal")
    public String home() { return "index"; }


    @GetMapping("/admin-login")
    public String adminLoginPage() {
        return "redirect:/admin/login";
    }

    @GetMapping("/user-login")
    public String userLoginPage() {
        return "user-login";
    }


    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
}
