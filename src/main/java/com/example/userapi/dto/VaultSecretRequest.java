package com.example.userapi.dto;

import java.util.Map;

public class VaultSecretRequest {
    private String path;
    private Map<String, String> data;

    // Getters and Setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
