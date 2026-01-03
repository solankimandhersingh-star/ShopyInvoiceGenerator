package com.shopy.generator.userservice.service;

import com.shopy.generator.userservice.dto.Organisation;
import com.shopy.generator.userservice.dto.ProfileDto;
import com.shopy.generator.userservice.entity.UserAccount;
import com.shopy.generator.userservice.entity.UserProfile;
import com.shopy.generator.userservice.repository.UserProfileRepository;
import com.shopy.generator.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserProfileRepository repo;
    private final UserRepository userRepository;

    public UserProfile get(Long userId) {
        return repo.findByUserId(userId).orElse(new UserProfile());
    }

    public ProfileDto getProfile(Long userId) {

        UserAccount user = userRepository.findById(userId)
                .orElseThrow();

        UserProfile profile = repo.findByUserId(userId)
                .orElse(new UserProfile());

        return ProfileDto.builder()
                .username(user.getUsername())
                .mobile(user.getMobile())

                .name(profile.getOrganisationName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .address(profile.getAddress())

                .bankName(profile.getBankName())
                .accountHolder(profile.getAccountHolder())
                .accountNumber(profile.getAccountNumber())
                .ifsc(profile.getIfsc())
                .upiId(profile.getUpiId())
                .build();
    }



    public void updateProfile(Long userId, ProfileDto dto) {

        UserProfile profile = repo.findByUserId(userId)
                .orElse(new UserProfile());

        profile.setUserId(userId);

        profile.setOrganisationName(dto.getName());
        profile.setEmail(dto.getEmail());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());

        profile.setBankName(dto.getBankName());
        profile.setAccountHolder(dto.getAccountHolder());
        profile.setAccountNumber(dto.getAccountNumber());
        profile.setIfsc(dto.getIfsc());
        profile.setUpiId(dto.getUpiId());

        repo.save(profile);
    }



}
