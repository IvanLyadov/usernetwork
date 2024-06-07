package com.example.userapi.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyTest {

    @Test
    void testGettersAndSetters() {
        Policy policy = new Policy();

        policy.setId(1L);
        policy.setName("Policy Name");
        policy.setDefinition("Policy Definition");

        Set<User> users = new HashSet<>();
        User user = new User();
        user.setUsername("user123");
        users.add(user);
        policy.setUsers(users);

        assertThat(policy.getId()).isEqualTo(1L);
        assertThat(policy.getName()).isEqualTo("Policy Name");
        assertThat(policy.getDefinition()).isEqualTo("Policy Definition");
        assertThat(policy.getUsers()).contains(user);
    }

    @Test
    void testNoArgsConstructor() {
        Policy policy = new Policy();

        assertThat(policy.getId()).isNull();
        assertThat(policy.getName()).isNull();
        assertThat(policy.getDefinition()).isNull();
        assertThat(policy.getUsers()).isNull();
    }
}
