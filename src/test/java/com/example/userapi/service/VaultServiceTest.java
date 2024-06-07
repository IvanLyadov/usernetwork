package com.example.userapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class VaultServiceTest {

    @Mock
    private VaultTemplate vaultTemplate;

    @Mock
    private VaultKeyValueOperations kvOperations;

    @InjectMocks
    private VaultService vaultService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(vaultTemplate.opsForKeyValue(anyString(), any(VaultKeyValueOperationsSupport.KeyValueBackend.class))).thenReturn(kvOperations);
    }

    @Test
    void testCreateOrUpdateSecret() {
        doNothing().when(kvOperations).put(anyString(), any(Map.class));

        Map<String, String> data = new HashMap<>();
        data.put("key", "value");

        vaultService.createOrUpdateSecret("path", data);
        verify(kvOperations, times(1)).put(anyString(), eq(data));
    }

    @Test
    void testReadSecret() {
        VaultResponse response = mock(VaultResponse.class);
        Map<String, Object> data = new HashMap<>();
        data.put("key", "value");

        when(response.getData()).thenReturn(data);
        when(kvOperations.get(anyString())).thenReturn(response);

        Map<String, Object> result = vaultService.readSecret("path");
        assertNotNull(result);
        assertEquals("value", result.get("key"));
    }

    @Test
    void testReadSecret_NoData() {
        when(kvOperations.get(anyString())).thenReturn(null);

        Map<String, Object> result = vaultService.readSecret("path");
        assertNull(result);
    }

    @Test
    void testDeleteSecret() {
        doNothing().when(kvOperations).delete(anyString());

        vaultService.deleteSecret("path");
        verify(kvOperations, times(1)).delete(anyString());
    }
}
