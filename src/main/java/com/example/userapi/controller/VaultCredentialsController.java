package com.example.userapi.controller;

import com.example.userapi.config.VaultConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vault")
public class VaultCredentialsController {

    @Autowired
    private VaultConfig vaultConfig;

    @GetMapping("/credentials")
    public String getVaultCredentials(Model model) {
        model.addAttribute("username", vaultConfig.getUser());
        model.addAttribute("password", vaultConfig.getSecret());
        return "vault-credentials";
    }
}
