package com.example.userapi.controller;

import com.example.userapi.model.Item;
import com.example.userapi.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<Item> getAllItems(@RequestHeader("userId") String userId, @RequestHeader("token") String token) {
        return itemService.getAllItems(userId, token);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id, @RequestHeader("userId") String userId, @RequestHeader("token") String token) {
        Optional<Item> item = itemService.getItemById(id, userId, token);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Item createItem(@RequestBody Item item, @RequestHeader("userId") String userId, @RequestHeader("token") String token) {
        return itemService.createItem(item, userId, token);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item itemDetails, @RequestHeader("userId") String userId, @RequestHeader("token") String token) {
        Item updatedItem = itemService.updateItem(id, itemDetails, userId, token);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id, @RequestHeader("userId") String userId, @RequestHeader("token") String token) {
        itemService.deleteItem(id, userId, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sector/{sectorId}")
    public List<Item> getItemsBySector(@PathVariable String sectorId, @RequestHeader("userId") String userId, @RequestHeader("token") String token) {
        return itemService.getItemsBySector(sectorId, userId, token);
    }
}
