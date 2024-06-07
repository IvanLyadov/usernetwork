package com.example.userapi.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testGettersAndSetters() {
        User user = new User();

        user.setId("1");
        user.setUsername("user123");
        user.setEmail("user@example.com");

        Set<Policy> policies = new HashSet<>();
        Policy policy = new Policy();
        policy.setName("Policy Name");
        policies.add(policy);
        user.setPolicies(policies);

        assertThat(user.getId()).isEqualTo("1");
        assertThat(user.getUsername()).isEqualTo("user123");
        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getPolicies()).contains(policy);
    }

    @Test
    void testNoArgsConstructor() {
        User user = new User();

        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getPolicies()).isNull();
    }
}
