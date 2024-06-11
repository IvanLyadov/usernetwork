package com.example.userapi.service;

import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VaultTemplate vaultTemplate;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findById(username);
    }

    public User createUser(User user) {
        // Generate a unique ID for the user
        String userId = UUID.randomUUID().toString();
        user.setId(userId);

        // Save user in Clickhouse
        User savedUser = userRepository.save(user);

        // Generate random token
        String token = UUID.randomUUID().toString();

        // Store token in Vault
        storeTokenInVault(savedUser.getId(), token);

        return savedUser;
    }

    public User updateUser(String username, User userDetails) {
        User user = userRepository.findById(username).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        User user = userRepository.findById(username).orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.deleteById(username);
        deleteTokenFromVault(user.getId());
    }

    private void storeTokenInVault(String userId, String token) {
        String path = "user-secrets/" + userId;
        Map<String, String> data = new HashMap<>();
        data.put("user", userId);
        data.put("secret", token);
        vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2).put(path, data);
    }

    private void deleteTokenFromVault(String userId) {
        String path = "user-secrets/" + userId;
        vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2).delete(path);
    }
}
