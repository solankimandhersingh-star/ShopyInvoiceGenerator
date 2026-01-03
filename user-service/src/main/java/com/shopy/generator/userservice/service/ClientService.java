package com.shopy.generator.userservice.service;

import com.shopy.generator.userservice.entity.Client;
import com.shopy.generator.userservice.entity.UserAccount;
import com.shopy.generator.userservice.repository.ClientRepository;
import com.shopy.generator.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repo;
    private final UserRepository userRepo;

    public List<Client> list(Long userId) {
        return repo.findByUserId(userId);
    }

    public void save(Long userId, Client client) {

        UserAccount user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        client.setUser(user);

        repo.save(client);
    }

    public Client get(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
