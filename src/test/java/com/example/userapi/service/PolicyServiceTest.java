package com.example.userapi.service;

import com.example.userapi.model.Policy;
import com.example.userapi.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPolicies() {
        List<Policy> policies = Arrays.asList(new Policy(), new Policy());
        when(policyRepository.findAll()).thenReturn(policies);

        List<Policy> result = policyService.getAllPolicies();
        assertEquals(2, result.size());
        verify(policyRepository, times(1)).findAll();
    }

    @Test
    void testGetPolicyById() {
        Policy policy = new Policy();
        when(policyRepository.findById(anyLong())).thenReturn(Optional.of(policy));

        Optional<Policy> result = policyService.getPolicyById(1L);
        assertTrue(result.isPresent());
        assertEquals(policy, result.get());
    }

    @Test
    void testCreatePolicy() {
        Policy policy = new Policy();
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        Policy result = policyService.createPolicy(policy);
        assertNotNull(result);
        verify(policyRepository, times(1)).save(policy);
    }

    @Test
    void testUpdatePolicy() {
        Policy existingPolicy = new Policy();
        existingPolicy.setName("Old Name");
        existingPolicy.setDefinition("Old Definition");

        when(policyRepository.findById(anyLong())).thenReturn(Optional.of(existingPolicy));
        when(policyRepository.save(any(Policy.class))).thenReturn(existingPolicy);

        Policy policyDetails = new Policy();
        policyDetails.setName("New Name");
        policyDetails.setDefinition("New Definition");

        Policy result = policyService.updatePolicy(1L, policyDetails);
        assertEquals("New Name", result.getName());
        assertEquals("New Definition", result.getDefinition());
    }

    @Test
    void testDeletePolicy() {
        doNothing().when(policyRepository).deleteById(anyLong());

        policyService.deletePolicy(1L);
        verify(policyRepository, times(1)).deleteById(1L);
    }
}
