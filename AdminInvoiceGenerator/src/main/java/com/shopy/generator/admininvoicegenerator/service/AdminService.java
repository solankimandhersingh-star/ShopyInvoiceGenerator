package com.shopy.generator.admininvoicegenerator.service;

import com.shopy.generator.admininvoicegenerator.Constants.ApprovalStatus;
import com.shopy.generator.admininvoicegenerator.Constants.Role;
import com.shopy.generator.admininvoicegenerator.entity.Invoice;
import com.shopy.generator.admininvoicegenerator.entity.Organisation;
import com.shopy.generator.admininvoicegenerator.entity.UserAccount;
import com.shopy.generator.admininvoicegenerator.repository.InvoiceRepository;
import com.shopy.generator.admininvoicegenerator.repository.OrganisationRepository;
import com.shopy.generator.admininvoicegenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final OrganisationRepository organisationRepo;
    private final UserRepository userRepo;
    private final InvoiceRepository invoiceRepo;
    private final UsernameGenerator usernameGenerator;
    private final PasswordEncoder passwordEncoder;

    public List<UserAccount> getPendingUsers() {
        return userRepo.findByApprovalStatus(ApprovalStatus.PENDING);
    }
    public List<Organisation> getPendingOrg() {
        return organisationRepo.findByStatus(ApprovalStatus.PENDING);
    }

    public UserAccount approveUser(Long userId) {
        UserAccount user = userRepo.findById(userId).orElseThrow();
        user.setApprovalStatus(ApprovalStatus.APPROVED);
        user.setEnabled(true);
        return userRepo.save(user);
    }

    public Organisation approveOrganisation(Long orgId) {
        Organisation org = organisationRepo.findById(orgId).orElseThrow();
        org.setStatus(ApprovalStatus.APPROVED);
        return organisationRepo.save(org);
    }

    public UserAccount createUser(Role role, String organisationName) {
        Organisation org = organisationRepo.findByName(organisationName).orElseGet(() -> organisationRepo.save(Organisation.builder().name(organisationName).status(ApprovalStatus.PENDING).build()));
        String username = usernameGenerator.generate();
        String encoded = passwordEncoder.encode("password");
        UserAccount user = UserAccount.builder().username(username).password(encoded).role(role).organisation(org).approvalStatus(ApprovalStatus.PENDING).firstLogin(true).enabled(true).build();
        return userRepo.save(user);
    }

    public List<Organisation> listApprovedOrganisations() {
        return organisationRepo.findByStatus(ApprovalStatus.APPROVED);
    }

    public List<Invoice> getInvoicesByOrganisation(String organisationName) {
        Organisation org = organisationRepo.findByName(organisationName).orElseThrow();
        return invoiceRepo.findByOrganisation(org);
    }

    public List<Organisation> listPendingOrganisations() {
        return organisationRepo.findByStatus(ApprovalStatus.PENDING);
    }

    public List<Organisation> search(String name) {
        if (name == null || name.isBlank()) {
            return organisationRepo.findAll();
        }
        return organisationRepo.findByNameContainingIgnoreCase(name.trim());
    }
}
