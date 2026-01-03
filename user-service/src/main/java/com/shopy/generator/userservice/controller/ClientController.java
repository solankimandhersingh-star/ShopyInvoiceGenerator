package com.shopy.generator.userservice.controller;

import com.shopy.generator.userservice.entity.Client;
import com.shopy.generator.userservice.service.ClientService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    // LIST
    @GetMapping
    public String list(Model model, HttpSession session) {

        Long userId = (Long) session.getAttribute("USER");

        model.addAttribute("clients", service.list(userId));

        return "user/clients";
    }

    // NEW CLIENT FORM
    @GetMapping("/new")
    public String newClient(Model model) {
        model.addAttribute("client", new Client());
        return "user/client-form";
    }

    // SAVE CLIENT
    @PostMapping("/save")
    public String save(@ModelAttribute Client client, HttpSession session) {

        Long userId = (Long) session.getAttribute("USER");

        service.save(userId, client);

        return "redirect:/user/clients?success";
    }

    // EDIT FORM
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("client", service.get(id));
        return "user/client-form";
    }

    // DELETE CLIENT
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/user/clients?deleted";
    }
}
