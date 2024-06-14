package com.example.userapi.controller;

import com.example.userapi.model.Item;
import com.example.userapi.service.ItemService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        item = new Item();
        item.setId(1L);
        item.setName("testItem");
        item.setDescription("This is a test item");
    }

    @Test
    public void testGetAllItems() {
        List<Item> items = Arrays.asList(item);
        when(itemService.getAllItems()).thenReturn(items);

        List<Item> result = itemController.getAllItems("userId", "token");

        assertEquals(1, result.size());
        assertEquals("testItem", result.get(0).getName());
    }

    @Test
    public void testGetItemById() {
        when(itemService.getItemById(anyLong(), anyString(), anyString())).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L, "userId", "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testItem", response.getBody().getName());
    }

    @Test
    public void testGetItemById_NotFound() {
        when(itemService.getItemById(anyLong(), anyString(), anyString())).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(1L, "userId", "token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateItem() {
        when(itemService.createItem(any(Item.class), anyString(), anyString())).thenReturn(item);

        Item result = itemController.createItem(item, "userId", "token");

        assertEquals("testItem", result.getName());
    }

    @Test
    public void testUpdateItem() {
        when(itemService.updateItem(anyLong(), any(Item.class), anyString(), anyString())).thenReturn(item);

        ResponseEntity<Item> response = itemController.updateItem(1L, item, "userId", "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testItem", response.getBody().getName());
    }

    @Test
    public void testDeleteItem() {
        doNothing().when(itemService).deleteItem(anyLong(), anyString(), anyString());

        ResponseEntity<Void> response = itemController.deleteItem(1L, "userId", "token");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testGetItemsBySector() {
        List<Item> items = Arrays.asList(item);
        when(itemService.getItemsBySector(anyString(), anyString(), anyString())).thenReturn(items);

        List<Item> result = itemController.getItemsBySector("sectorId", "userId", "token");

        assertEquals(1, result.size());
        assertEquals("testItem", result.get(0).getName());
    }
}
