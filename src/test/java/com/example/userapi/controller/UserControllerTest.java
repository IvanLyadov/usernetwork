package com.example.userapi.controller;

import com.example.userapi.model.User;
import com.example.userapi.service.UserService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testEndpoint() {
        // When
        ResponseEntity<String> response = userController.testEndpoint();

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Test endpoint is working", response.getBody());
    }

    @Test
    void getAllUsers() {
        // Given
        User user1 = new User();
        user1.setId("1");
        User user2 = new User();
        user2.setId("2");
        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // When
        List<User> result = userController.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserByUsername() {
        // Given
        User user = new User();
        user.setId("1");
        user.setUsername("testuser");
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.of(user));

        // When
        ResponseEntity<User> response = userController.getUserByUsername("testuser");

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
        verify(userService, times(1)).getUserByUsername(anyString());
    }

    @Test
    void getUserByUsername_NotFound() {
        // Given
        when(userService.getUserByUsername(anyString())).thenReturn(Optional.empty());

        // When
        ResponseEntity<User> response = userController.getUserByUsername("testuser");

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(userService, times(1)).getUserByUsername(anyString());
    }

    @Test
    void createUser() {
        // Given
        User user = new User();
        user.setId("1");
        when(userService.createUser(any(User.class))).thenReturn(user);

        // When
        User createdUser = userController.createUser(user);

        // Then
        assertNotNull(createdUser);
        assertEquals("1", createdUser.getId());
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUser() {
        // Given
        User user = new User();
        user.setId("1");
        user.setUsername("updateduser");
        when(userService.updateUser(anyString(), any(User.class))).thenReturn(user);

        // When
        ResponseEntity<User> response = userController.updateUser("testuser", user);

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("updateduser", response.getBody().getUsername());
        verify(userService, times(1)).updateUser(anyString(), any(User.class));
    }

    @Test
    void deleteUser() {
        // When
        ResponseEntity<Void> response = userController.deleteUser("testuser");

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(userService, times(1)).deleteUser(anyString());
    }
}
