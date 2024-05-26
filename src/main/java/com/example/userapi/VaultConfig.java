package com.example.userapi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;


@Configuration
@ConfigurationProperties("clickhouse")
public class VaultConfig {
    @Autowired
    private VaultTemplate vaultTemplate;
    private String username;
    private String password;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @PostConstruct
    public void init() {
        VaultResponse response = vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2).get("demoapi");
        if (response != null && response.getData() != null) {
            this.username = (String) response.getData().get("username");
            this.password = (String) response.getData().get("password");
        }
        System.out.println("ClickhouseConfig - Username: " + username);
        System.out.println("ClickhouseConfig - Password: " + password);
    }
}
