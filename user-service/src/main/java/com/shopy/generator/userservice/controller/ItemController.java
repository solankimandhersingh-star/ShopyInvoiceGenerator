package com.shopy.generator.userservice.controller;

import com.shopy.generator.userservice.entity.Item;
import com.shopy.generator.userservice.service.ItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @GetMapping
    public String list(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("USER");
        model.addAttribute("items", service.list(userId));
        return "user/items";
    }

    @PostMapping
    public String save(@ModelAttribute Item item, HttpSession session) {
        Long userId = (Long) session.getAttribute("USER");
        service.save(userId, item);
        return "redirect:/user/items";
    }
}

