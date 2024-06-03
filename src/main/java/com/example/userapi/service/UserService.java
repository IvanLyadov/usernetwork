package com.example.userapi.service;

//import com.example.userapi.config.VaultConfig;
import com.example.userapi.config.ClickhouseConfig;
import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
//    private VaultConfig vaultConfig;
    private ClickhouseConfig clickhouseConfig;
    public List<User> getAllUsers() {
        // For debugging purposes, print the Clickhouse credentials
//        System.out.println("Clickhouse Username: " + vaultConfig.getUsername());
//        System.out.println("Clickhouse Password: " + vaultConfig.getPassword());
        String clickhouseUsername = clickhouseConfig.getUsername();
        String clickhousePassword = clickhouseConfig.getPassword();

        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findById(username);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(String username, User userDetails) {
        User user = userRepository.findById(username).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }
}
