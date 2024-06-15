package com.example.userapi.controller;

import com.example.userapi.model.Item;
import com.example.userapi.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllItems() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");

        when(itemService.getAllItems(anyString(), anyString())).thenReturn(List.of(item));

        List<Item> items = itemController.getAllItems("userId", "token");

        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    @Test
    public void testGetItemById() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");

        when(itemService.getItemById(anyLong(), anyString(), anyString())).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L, "userId", "token");

        assertEquals(ResponseEntity.ok(item), response);
    }

    @Test
    public void testGetItemById_NotFound() {
        when(itemService.getItemById(anyLong(), anyString(), anyString())).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(1L, "userId", "token");

        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    public void testCreateItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");

        when(itemService.createItem(any(Item.class), anyString(), anyString())).thenReturn(item);

        Item createdItem = itemController.createItem(item, "userId", "token");

        assertEquals(item, createdItem);
    }

    @Test
    public void testUpdateItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");

        when(itemService.updateItem(anyLong(), any(Item.class), anyString(), anyString())).thenReturn(item);

        ResponseEntity<Item> response = itemController.updateItem(1L, item, "userId", "token");

        assertEquals(ResponseEntity.ok(item), response);
    }

    @Test
    public void testDeleteItem() {
        ResponseEntity<Void> response = itemController.deleteItem(1L, "userId", "token");

        assertEquals(ResponseEntity.noContent().build(), response);
    }

    @Test
    public void testGetItemsBySector() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Item1");

        when(itemService.getItemsBySector(anyString(), anyString(), anyString())).thenReturn(List.of(item));

        List<Item> items = itemController.getItemsBySector("sectorId", "userId", "token");

        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }
}
