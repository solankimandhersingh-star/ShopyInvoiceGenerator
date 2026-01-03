package com.shopy.generator.admininvoicegenerator.controller;

import com.shopy.generator.admininvoicegenerator.Constants.Role;
import com.shopy.generator.admininvoicegenerator.entity.Organisation;
import com.shopy.generator.admininvoicegenerator.entity.RegisteredUser;
import com.shopy.generator.admininvoicegenerator.entity.UserAccount;
import com.shopy.generator.admininvoicegenerator.repository.OrganisationRepository;
import com.shopy.generator.admininvoicegenerator.service.AdminService;
import com.shopy.generator.admininvoicegenerator.service.RegistrationApprovalService;
import com.shopy.generator.admininvoicegenerator.service.RegistrationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final RegistrationClient registrationClient;
    private final RegistrationApprovalService registrationApprovalService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pendingUsers", adminService.getPendingUsers());
        model.addAttribute("pending org", adminService.getPendingOrg());
        return "admin/dashboard";
    }

    @PostMapping("/approve/user/{id}")
    public String approveUser(@PathVariable Long id) {
        adminService.approveUser(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/approve/org/{id}")
    public String approveOrg(@PathVariable Long id) {
        adminService.approveOrganisation(id);
        return "redirect:/admin/dashboard";
    }


    @GetMapping("/approvals")
    public String approvals(Model model) {
        model.addAttribute("pendingUsers", adminService.getPendingUsers());
        model.addAttribute("pendingRegistrations", registrationClient.getPendingRegistrations());
        model.addAttribute("pendingOrgs", adminService.listPendingOrganisations());
        return "admin/approvals";
    }


    @GetMapping("/new-user")
    public String newUserForm(Model model) {
        model.addAttribute("roles", Role.values());
        return "admin/new-user";
    }

    @PostMapping("/new-user")
    public String createUser(@RequestParam Role role, @RequestParam String organisationName, Model model) {
        UserAccount created = adminService.createUser(role, organisationName);
        model.addAttribute("createdUser", created);
        return "admin/new-user-success";
    }

    @GetMapping("/invoices")
    public String invoices(Model model, @RequestParam(required = false) String organisationName) {
        List<Organisation> orgs = adminService.listApprovedOrganisations();
        model.addAttribute("organisations", orgs);
        if (organisationName != null && !organisationName.isBlank()) {
            model.addAttribute("selectedOrg", organisationName);
            model.addAttribute("invoices", adminService.getInvoicesByOrganisation(organisationName));
        }
        return "admin/invoices";
    }

    @GetMapping("/search-org")
    public String searchOrg(
            @RequestParam(value = "q", required = false) String q,
            Model model) {

        List<Organisation> results = adminService.search(q);

        model.addAttribute("q", q);
        model.addAttribute("results", results);

        return "admin/search-org";
    }
    @PostMapping("/approve/registration/{id}")
    public String approveRegistration(@PathVariable Long id) {
        registrationApprovalService.approve(id);
        return "redirect:/admin/approvals";
    }

}
