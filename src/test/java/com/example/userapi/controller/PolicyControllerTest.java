package com.example.userapi.controller;

import com.example.userapi.model.Policy;
import com.example.userapi.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PolicyControllerTest {

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private PolicyController policyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllPolicies() {
        // Given
        Policy policy1 = new Policy();
        policy1.setId(1L);
        Policy policy2 = new Policy();
        policy2.setId(2L);
        List<Policy> policies = Arrays.asList(policy1, policy2);
        when(policyService.getAllPolicies()).thenReturn(policies);

        // When
        List<Policy> result = policyController.getAllPolicies();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(policyService, times(1)).getAllPolicies();
    }

    @Test
    void getPolicyById() {
        // Given
        Policy policy = new Policy();
        policy.setId(1L);
        when(policyService.getPolicyById(anyLong())).thenReturn(Optional.of(policy));

        // When
        ResponseEntity<Policy> response = policyController.getPolicyById(1L);

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(policyService, times(1)).getPolicyById(anyLong());
    }

    @Test
    void getPolicyById_NotFound() {
        // Given
        when(policyService.getPolicyById(anyLong())).thenReturn(Optional.empty());

        // When
        ResponseEntity<Policy> response = policyController.getPolicyById(1L);

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(policyService, times(1)).getPolicyById(anyLong());
    }

    @Test
    void createPolicy() {
        // Given
        Policy policy = new Policy();
        policy.setId(1L);
        when(policyService.createPolicy(any(Policy.class))).thenReturn(policy);

        // When
        Policy createdPolicy = policyController.createPolicy(policy);

        // Then
        assertNotNull(createdPolicy);
        assertEquals(1L, createdPolicy.getId());
        verify(policyService, times(1)).createPolicy(any(Policy.class));
    }

    @Test
    void updatePolicy() {
        // Given
        Policy policy = new Policy();
        policy.setId(1L);
        when(policyService.updatePolicy(anyLong(), any(Policy.class))).thenReturn(policy);

        // When
        ResponseEntity<Policy> response = policyController.updatePolicy(1L, policy);

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(policyService, times(1)).updatePolicy(anyLong(), any(Policy.class));
    }

    @Test
    void deletePolicy() {
        // When
        ResponseEntity<Void> response = policyController.deletePolicy(1L);

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(policyService, times(1)).deletePolicy(anyLong());
    }
}

