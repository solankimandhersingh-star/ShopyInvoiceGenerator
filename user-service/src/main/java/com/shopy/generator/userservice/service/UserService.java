package com.shopy.generator.userservice.service;

import com.shopy.generator.userservice.entity.UserAccount;
import com.shopy.generator.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserAccount authenticate(String username, String password) {

        UserAccount user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public void changePassword(Long id, String newPassword) {
        UserAccount user = repo.findById(id).orElseThrow();
        user.setPassword(encoder.encode(newPassword));
        user.setFirstLogin(false);
        repo.save(user);
    }

    public UserAccount findByUsernameOrMobile(String value) {
        return repo.findByUsername(value)
                .or(() -> repo.findByMobile(value))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }



    public void updatePassword(String username, String newPassword) {

        UserAccount user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(encoder.encode(newPassword));
        user.setFirstLogin(false);

        repo.save(user);
    }
}
