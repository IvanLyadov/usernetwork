package com.example.userapi.controller;

import com.example.userapi.model.User;
import com.example.userapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId("1");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = Arrays.asList(user);
        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = userController.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    public void testGetUserByUsername() {
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserByUsername("testuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    public void testGetUserByUsername_NotFound() {
        when(userService.getUserByUsername("testuser")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserByUsername("testuser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateUser() {
        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("testuser", ((User) response.getBody()).getUsername());
    }

    @Test
    public void testCreateUser_Exception() {
        when(userService.createUser(any(User.class))).thenThrow(new RuntimeException("Error creating user"));

        ResponseEntity<?> response = userController.createUser(user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error creating user", response.getBody());
    }

    @Test
    public void testUpdateUser() {
        when(userService.updateUser(anyString(), any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.updateUser("testuser", user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    public void testUpdateUser_Exception() {
        when(userService.updateUser(anyString(), any(User.class))).thenThrow(new RuntimeException("Error updating user"));

        ResponseEntity<User> response = userController.updateUser("testuser", user);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userService).deleteUser("testuser");

        ResponseEntity<Void> response = userController.deleteUser("testuser");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteUser_Exception() {
        doThrow(new RuntimeException("Error deleting user")).when(userService).deleteUser("testuser");

        ResponseEntity<Void> response = userController.deleteUser("testuser");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
