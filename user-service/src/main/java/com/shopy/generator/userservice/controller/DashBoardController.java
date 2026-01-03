package com.shopy.generator.userservice.controller;


import com.shopy.generator.userservice.repository.ClientRepository;
import com.shopy.generator.userservice.repository.InvoiceRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class DashBoardController {

    private final ClientRepository clientRepository;
    private final InvoiceRepository invoiceRepository;

    @GetMapping("/home")
    public String dashboard(Model model, HttpSession httpSession) {

        Long userId = (Long) httpSession.getAttribute("USER");
        long clientCount = clientRepository.countByUserId(userId);

        // TODO when invoices ready — replace 0
        long invoiceCount = invoiceRepository.countByUserId(userId);
        long pendingPayments = 0;

        model.addAttribute("clientCount", clientCount);
        model.addAttribute("invoiceCount", invoiceCount);
        model.addAttribute("pendingPayments", pendingPayments);
        return "user/home";
    }

    @GetMapping("/invoices")
    public String myInvoices() {
        return "user/invoices";
    }


}
