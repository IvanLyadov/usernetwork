package com.example.userapi.service;

import com.example.userapi.model.Item;
import com.example.userapi.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ItemService {

    private static final Logger logger = Logger.getLogger(ItemService.class.getName());

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private VaultTemplate vaultTemplate;

    private VaultKeyValueOperations kvOperations;

    @Autowired
    public void setVaultTemplate(VaultTemplate vaultTemplate) {
        this.kvOperations = vaultTemplate.opsForKeyValue("secret", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
    }

    @Transactional(readOnly = true)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Item> getItemById(Long id, String token) {
        validateToken(token);
        return itemRepository.findById(id);
    }

    @Transactional
    public Item createItem(Item item, String token) {
        String userId = validateTokenAndGetUserId(token);
        item.setUserId(userId);
        item.setId(generateItemId());
        logger.info("Creating item: " + item.toString());
        Item savedItem = itemRepository.save(item);
        logger.info("Item created with ID: " + savedItem.getId());
        return savedItem;
    }

    @Transactional
    public Item updateItem(Long id, Item itemDetails, String token) {
        validateToken(token);
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        item.setDescription(itemDetails.getDescription());
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id, String token) {
        validateToken(token);
        itemRepository.deleteById(id);
    }

    private void validateToken(String token) {
        VaultResponse response = kvOperations.get("demoapi");
        if (response == null || response.getData() == null) {
            throw new RuntimeException("Invalid token");
        }
        String storedToken = (String) response.getData().get("secret");
        if (!token.equals(storedToken)) {
            throw new RuntimeException("Invalid token");
        }
    }

    private String validateTokenAndGetUserId(String token) {
        VaultResponse response = kvOperations.get("demoapi");
        if (response == null || response.getData() == null) {
            throw new RuntimeException("Invalid token");
        }
        String storedToken = (String) response.getData().get("secret");
        if (!token.equals(storedToken)) {
            throw new RuntimeException("Invalid token");
        }
        return (String) response.getData().get("user");
    }

    private Long generateItemId() {
        // Implement your logic to generate a unique ID
        // For example, you can use a sequence or a combination of current timestamp and a random number
        return System.currentTimeMillis(); // Just an example
    }
}
