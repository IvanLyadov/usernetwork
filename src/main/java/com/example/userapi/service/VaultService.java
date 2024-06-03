package com.example.userapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.HashMap;
import java.util.Map;

@Service
public class VaultService {

    @Autowired
    private VaultTemplate vaultTemplate;

    public void createOrUpdateSecret(String path, Map<String, String> data) {
        vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2)
                .put(path, data);
    }

    public Map<String, Object> readSecret(String path) {
        VaultResponse response = vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2)
                .get(path);
        return response != null ? response.getData() : null;
    }

    public void deleteSecret(String path) {
        vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2)
                .delete(path);
    }
}
