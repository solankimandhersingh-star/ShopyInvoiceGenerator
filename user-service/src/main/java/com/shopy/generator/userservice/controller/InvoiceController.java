package com.shopy.generator.userservice.controller;

import com.shopy.generator.userservice.service.ClientService;
import com.shopy.generator.userservice.service.InvoiceService;
import com.shopy.generator.userservice.service.ItemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final ClientService clientService;
    private final ItemService itemService;

    @GetMapping("/new")
    public String newInvoice(Model model, HttpSession session) {

        Long userId = (Long) session.getAttribute("USER");

        model.addAttribute("clients", clientService.list(userId));
        model.addAttribute("items", itemService.list(userId));

        return "user/invoice-new";
    }

    @PostMapping("/new")
    public String saveInvoice(
            HttpSession session,

            @RequestParam Long clientId,
            @RequestParam Double subtotal,

            @RequestParam(required = false) Double cgstPercent,
            @RequestParam(required = false) Double sgstPercent,
            @RequestParam(required = false) Double igstPercent,

            @RequestParam Double discountPercent,
            @RequestParam Double discountAmount,

            @RequestParam Double totalAmount,

            @RequestParam("itemName") List<String> itemNames,
            @RequestParam("quantity") List<Integer> quantities,
            @RequestParam("unitPrice") List<Double> unitPrices
    ) {

        Long userId = (Long) session.getAttribute("USER");

        invoiceService.saveInvoice(
                userId,
                clientId,
                subtotal,
                cgstPercent,
                sgstPercent,
                igstPercent,
                discountPercent,
                discountAmount,
                totalAmount,
                itemNames,
                quantities,
                unitPrices
        );

        return "redirect:/user/invoice/list?success";
    }

    @GetMapping("/list")
    public String listInvoices(Model model, HttpSession session) {

        Long userId = (Long) session.getAttribute("USER");
        model.addAttribute("invoices", invoiceService.userInvoices(userId));

        return "user/invoice-list";
    }

    @GetMapping("/view/pdf/{id}")
    public ResponseEntity<byte[]> viewInvoicePdf(@PathVariable Long id) throws Exception {

        byte[] pdf = invoiceService.generateInvoicePdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=invoice.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
