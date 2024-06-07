package com.example.userapi.service;

import com.example.userapi.model.User;
import com.example.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VaultTemplate vaultTemplate;

    @Mock
    private VaultKeyValueOperations kvOperations;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(vaultTemplate.opsForKeyValue(anyString(), any(VaultKeyValueOperationsSupport.KeyValueBackend.class))).thenReturn(kvOperations);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserByUsername() {
        User user = new User();
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByUsername("username");
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setUsername("username");
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(kvOperations).put(anyString(), any());

        User result = userService.createUser(user);
        assertNotNull(result);
        assertNotNull(result.getId());
        verify(userRepository, times(1)).save(user);
        verify(kvOperations, times(1)).put(anyString(), any());
    }

    @Test
    void testUpdateUser() {
        User existingUser = new User();
        existingUser.setUsername("username");
        existingUser.setEmail("old@example.com");

        when(userRepository.findById(anyString())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User userDetails = new User();
        userDetails.setEmail("new@example.com");

        User result = userService.updateUser("username", userDetails);
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(anyString());
        doNothing().when(kvOperations).delete(anyString());

        userService.deleteUser("username");
        verify(userRepository, times(1)).deleteById("username");
        verify(kvOperations, times(1)).delete(anyString());
    }
}
