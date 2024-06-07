package com.example.userapi.controller;

import com.example.userapi.config.VaultConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VaultCredentialsControllerTest {

    @Mock
    private VaultConfig vaultConfig;

    @Mock
    private Model model;

    @InjectMocks
    private VaultCredentialsController vaultCredentialsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getVaultCredentials() {
        // Given
        when(vaultConfig.getUser()).thenReturn("testuser");
        when(vaultConfig.getSecret()).thenReturn("testsecret");

        // When
        String viewName = vaultCredentialsController.getVaultCredentials(model);

        // Then
        assertNotNull(viewName);
        assertEquals("vault-credentials", viewName);
        verify(model, times(1)).addAttribute("username", "testuser");
        verify(model, times(1)).addAttribute("password", "testsecret");
        verify(vaultConfig, times(1)).getUser();
        verify(vaultConfig, times(1)).getSecret();
    }
}
