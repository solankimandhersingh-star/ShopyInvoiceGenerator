package com.shopy.generator.userservice.service;

import com.shopy.generator.userservice.entity.Item;
import com.shopy.generator.userservice.entity.UserAccount;
import com.shopy.generator.userservice.repository.ItemRepository;
import com.shopy.generator.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repo;
    private final UserRepository userRepository;

    public void save(Long userId, Item item) {

        UserAccount user = userRepository.findById(userId).orElseThrow();
        item.setUser(user);

        repo.save(item);
    }

    public List<Item> list(Long userId) {
        return repo.findByUserId(userId);
    }
}

