package com.shopy.generator.poratlservice.controller;

import com.shopy.generator.poratlservice.dto.RegistrationForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class PortalRegistrationController {

    @GetMapping("/portal/register")
    public String showForm(RegistrationForm form) {
        return "portal/register";
    }

    @PostMapping("/portal/register")
    public String submit(RegistrationForm form) {

        RestTemplate rest = new RestTemplate();

        rest.postForObject(
                "http://localhost:8080/registration",
                form,
                Void.class
        );

        return "portal/register-success";
    }
}
