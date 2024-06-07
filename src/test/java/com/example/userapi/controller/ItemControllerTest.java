package com.example.userapi.controller;

import com.example.userapi.model.Item;
import com.example.userapi.service.ItemService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllItems() {
        // Given
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(2L);
        List<Item> items = Arrays.asList(item1, item2);
        when(itemService.getAllItems()).thenReturn(items);

        // When
        List<Item> result = itemController.getAllItems("token");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemService, times(1)).getAllItems();
    }

    @Test
    void getItemById() {
        // Given
        Item item = new Item();
        item.setId(1L);
        when(itemService.getItemById(anyLong(), anyString())).thenReturn(Optional.of(item));

        // When
        ResponseEntity<Item> response = itemController.getItemById(1L, "token");

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(itemService, times(1)).getItemById(anyLong(), anyString());
    }

    @Test
    void getItemById_NotFound() {
        // Given
        when(itemService.getItemById(anyLong(), anyString())).thenReturn(Optional.empty());

        // When
        ResponseEntity<Item> response = itemController.getItemById(1L, "token");

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(itemService, times(1)).getItemById(anyLong(), anyString());
    }

    @Test
    void createItem() {
        // Given
        Item item = new Item();
        item.setId(1L);
        when(itemService.createItem(any(Item.class), anyString())).thenReturn(item);

        // When
        Item createdItem = itemController.createItem(item, "token");

        // Then
        assertNotNull(createdItem);
        assertEquals(1L, createdItem.getId());
        verify(itemService, times(1)).createItem(any(Item.class), anyString());
    }

    @Test
    void updateItem() {
        // Given
        Item item = new Item();
        item.setId(1L);
        when(itemService.updateItem(anyLong(), any(Item.class), anyString())).thenReturn(item);

        // When
        ResponseEntity<Item> response = itemController.updateItem(1L, item, "token");

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(itemService, times(1)).updateItem(anyLong(), any(Item.class), anyString());
    }

    @Test
    void deleteItem() {
        // When
        ResponseEntity<Void> response = itemController.deleteItem(1L, "token");

        // Then
        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(itemService, times(1)).deleteItem(anyLong(), anyString());
    }

    @Test
    void getItemsBySector() {
        // Given
        Item item1 = new Item();
        item1.setId(1L);
        Item item2 = new Item();
        item2.setId(2L);
        List<Item> items = Arrays.asList(item1, item2);
        when(itemService.getItemsBySector(anyString(), anyString())).thenReturn(items);

        // When
        List<Item> result = itemController.getItemsBySector("sector1", "token");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(itemService, times(1)).getItemsBySector(anyString(), anyString());
    }
}
