package com.shopy.generator.admininvoicegenerator.SecurityConfiguration;

import com.shopy.generator.admininvoicegenerator.Constants.ApprovalStatus;
import com.shopy.generator.admininvoicegenerator.Constants.Role;
import com.shopy.generator.admininvoicegenerator.entity.Organisation;
import com.shopy.generator.admininvoicegenerator.entity.UserAccount;
import com.shopy.generator.admininvoicegenerator.repository.OrganisationRepository;
import com.shopy.generator.admininvoicegenerator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InMemoryAdminBootstrap implements CommandLineRunner {
    private final UserRepository userRepo;
    private final OrganisationRepository orgRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        // Ensure at least one ADMIN account exists (as per your spec)
        if (userRepo.findByUsername("ADMIN").isEmpty()) {
            Organisation org = orgRepo.findByName("GLOBAL").orElseGet(() -> orgRepo.save(Organisation.builder().name("GLOBAL").status(ApprovalStatus.APPROVED).build()));
            UserAccount admin = UserAccount.builder().username("ADMIN").password(encoder.encode("password")).role(Role.ADMIN).organisation(org).approvalStatus(ApprovalStatus.APPROVED).enabled(true).firstLogin(false).build();
            userRepo.save(admin);
        }
    }

}
