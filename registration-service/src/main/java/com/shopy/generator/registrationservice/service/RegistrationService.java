package com.shopy.generator.registrationservice.service;

import com.shopy.generator.registrationservice.dto.RegistrationRequest;
import com.shopy.generator.registrationservice.entity.Organisation;
import com.shopy.generator.registrationservice.entity.RegisteredUser;
import com.shopy.generator.registrationservice.repository.OrganisationRepository;
import com.shopy.generator.registrationservice.repository.RegisteredUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final OrganisationRepository orgRepo;
    private final RegisteredUserRepository userRepo;

    public RegisteredUser register(RegistrationRequest req) {

        Organisation org = new Organisation();
        org.setName(req.getOrgName());
        org.setGstNumber(req.getGstNumber());
        org.setAddress(req.getAddress());
        org.setEmail(req.getOrgEmail());
        org.setPhone(req.getPhone());
        org.setStateName(req.getStateName());
        org.setStateCode(req.getStateCode());
        org.setStatus("PENDING");

        Organisation savedOrg = orgRepo.save(org);

        RegisteredUser user = new RegisteredUser();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setMobile(req.getMobile());
        user.setOrganisation(savedOrg);
        user.setStatus("PENDING");

        return userRepo.save(user);
    }
}
