package com.example.userapi.service;

import com.example.userapi.model.Policy;
import com.example.userapi.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Optional<Policy> getPolicyById(Long id) {
        return policyRepository.findById(id);
    }

    public Policy createPolicy(Policy policy) {
        return policyRepository.save(policy);
    }

    public Policy updatePolicy(Long id, Policy policyDetails) {
        Policy policy = policyRepository.findById(id).orElseThrow(() -> new RuntimeException("Policy not found"));
        policy.setName(policyDetails.getName());
        policy.setDefinition(policyDetails.getDefinition());
        return policyRepository.save(policy);
    }

    public void deletePolicy(Long id) {
        policyRepository.deleteById(id);
    }
}
