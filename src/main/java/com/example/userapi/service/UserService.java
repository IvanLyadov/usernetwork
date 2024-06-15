package com.example.userapi.service;

import com.example.userapi.model.Policy;
import com.example.userapi.model.User;
import com.example.userapi.repository.PolicyRepository;
import com.example.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private VaultTemplate vaultTemplate;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findById(username);
    }

    public User createUser(User user) {
        logger.info("Starting to create user");

        if (userRepository.existsByUsername(user.getUsername())) {
            logger.error("User with username {} already exists", user.getUsername());
            throw new RuntimeException("User with username " + user.getUsername() + " already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            logger.error("User with email {} already exists", user.getEmail());
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }

        try {
            // Generate a unique ID for the user
            String userId = UUID.randomUUID().toString();
            user.setId(userId);
            logger.info("Generated user ID: {}", userId);

            // Fetch the default policy with ID 3
            Policy defaultPolicy = policyRepository.findById(3L).orElseThrow(() -> new RuntimeException("Default policy not found"));
            logger.info("Fetched default policy with ID 3");

            // Assign the default policy to the new user
            user.setPolicies(Set.of(defaultPolicy));
            logger.info("Assigned default policy to user");

            // Save user in Clickhouse
            User savedUser = userRepository.save(user);
            logger.info("Saved user in Clickhouse");

            // Generate random token
            String token = UUID.randomUUID().toString();
            logger.info("Generated token for user");

            // Store token in Vault
            storeTokenInVault(savedUser.getId(), token);
            logger.info("Stored token in Vault");

            logger.info("User created successfully with ID: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            logger.error("Error creating user", e);
            throw new RuntimeException("Error creating user: " + e.getMessage());
        }
    }

    public User updateUser(String username, User userDetails) {
        logger.info("Starting to update user with username: {}", username);
        try {
            User user = userRepository.findById(username).orElseThrow(() -> new RuntimeException("User not found"));
            user.setEmail(userDetails.getEmail());
            User updatedUser = userRepository.save(user);
            logger.info("User updated successfully with username: {}", updatedUser.getUsername());
            return updatedUser;
        } catch (Exception e) {
            logger.error("Error updating user", e);
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }

    public void deleteUser(String username) {
        logger.info("Starting to delete user with username: {}", username);
        try {
            User user = userRepository.findById(username).orElseThrow(() -> new RuntimeException("User not found"));
            userRepository.deleteById(username);
            deleteTokenFromVault(user.getId());
            logger.info("User deleted successfully with username: {}", username);
        } catch (Exception e) {
            logger.error("Error deleting user", e);
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }

    private void storeTokenInVault(String userId, String token) {
        logger.info("Storing token in Vault for user ID: {}", userId);
        String path = "user-secrets/" + userId;
        Map<String, String> data = new HashMap<>();
        data.put("user", userId);
        data.put("secret", token);
        vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2).put(path, data);
        logger.info("Token stored in Vault for user ID: {}", userId);
    }

    private void deleteTokenFromVault(String userId) {
        logger.info("Deleting token from Vault for user ID: {}", userId);
        String path = "user-secrets/" + userId;
        vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2).delete(path);
        logger.info("Token deleted from Vault for user ID: {}", userId);
    }
}
