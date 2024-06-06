package com.example.userapi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

@Configuration
@ConditionalOnProperty(name = "spring.cloud.vault.enabled", havingValue = "true")
public class VaultConfig {
    @Autowired
    private VaultTemplate vaultTemplate;

    private String secret;
    private String user;

    // Getters and Setters
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @PostConstruct
    public void init() {
        VaultResponse response = vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2).get("demoapi");
        if (response != null && response.getData() != null) {

            this.secret = (String) response.getData().get("secret");
            this.user = (String) response.getData().get("user");
        }
        System.out.println("VaultConfig - secret: " + secret);
        System.out.println("VaultConfig - user: " + user);
    }
}
