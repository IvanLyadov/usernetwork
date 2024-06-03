package com.example.userapi.controller;

import com.example.userapi.config.VaultConfig;
import com.example.userapi.dto.VaultSecretRequest;
import com.example.userapi.model.User;
import com.example.userapi.service.UserService;
import com.example.userapi.service.VaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VaultConfig vaultConfig;

    @Autowired
    private VaultService vaultService;

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Test endpoint is working");
    }

    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(username, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vault-credentials")
    public ResponseEntity<Map<String, String>> getVaultCredentials() {
        Map<String, String> credentials = Map.of(
                "username", vaultConfig.getUsername(),
                "password", vaultConfig.getPassword()
        );
        return ResponseEntity.ok(credentials);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createOrUpdateSecret(@RequestBody VaultSecretRequest request) {
        vaultService.createOrUpdateSecret(request.getPath(), request.getData());
        return ResponseEntity.ok("Secret created/updated successfully");
    }
}
