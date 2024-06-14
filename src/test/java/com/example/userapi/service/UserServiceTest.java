package com.example.userapi.service;

import com.example.userapi.model.Policy;
import com.example.userapi.model.User;
import com.example.userapi.repository.PolicyRepository;
import com.example.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private VaultTemplate vaultTemplate;

    @InjectMocks
    private UserService userService;

    private User user;
    private Policy policy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId("1");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        policy = new Policy();
        policy.setId(3L);
        policy.setName("defaultPolicy");
        policy.setDefinition("{\"allowedOperations\": [\"create\", \"update\"]}");
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    public void testGetUserByUsername() {
        when(userRepository.findById("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByUsername("testuser");

        assertEquals(true, result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    public void testCreateUser() {
        when(policyRepository.findById(3L)).thenReturn(Optional.of(policy));
        when(userRepository.save(any(User.class))).thenReturn(user);
        VaultKeyValueOperations kvOperations = mock(VaultKeyValueOperations.class);
        when(vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2)).thenReturn(kvOperations);

        User result = userService.createUser(user);

        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
        verify(vaultTemplate, times(1)).opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
        verify(kvOperations, times(1)).put(anyString(), any(Map.class));
    }

    @Test
    public void testCreateUser_PolicyNotFound() {
        when(policyRepository.findById(3L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Error creating user: Default policy not found", exception.getMessage());
    }

    @Test
    public void testUpdateUser() {
        when(userRepository.findById("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUser("testuser", user);

        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        when(userRepository.findById("testuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser("testuser", user);
        });

        assertEquals("Error updating user: User not found", exception.getMessage());
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.findById("testuser")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById("testuser");
        VaultKeyValueOperations kvOperations = mock(VaultKeyValueOperations.class);
        when(vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2)).thenReturn(kvOperations);

        userService.deleteUser("testuser");

        verify(userRepository, times(1)).deleteById("testuser");
        verify(vaultTemplate, times(1)).opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
        verify(kvOperations, times(1)).delete(anyString());
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        when(userRepository.findById("testuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUser("testuser");
        });

        assertEquals("Error deleting user: User not found", exception.getMessage());
    }
}
