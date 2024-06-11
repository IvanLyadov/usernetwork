package com.example.userapi.service;

import com.example.userapi.model.Item;
import com.example.userapi.model.Policy;
import com.example.userapi.model.User;
import com.example.userapi.repository.ItemRepository;
import com.example.userapi.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class ItemService {

    private static final Logger logger = Logger.getLogger(ItemService.class.getName());

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

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
        validatePolicy(userId, "create", item.getSectorId());
        item.setUserId(userId); // Set the user ID instead of the username
        item.setId(generateItemId());
        logger.info("Creating item: " + item.toString());
        Item savedItem = itemRepository.save(item);
        logger.info("Item created with ID: " + savedItem.getId());
        return savedItem;
    }

    @Transactional
    public Item updateItem(Long id, Item itemDetails, String token) {
        String userId = validateTokenAndGetUserId(token);
        validatePolicy(userId, "update", itemDetails.getSectorId());
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        item.setDescription(itemDetails.getDescription());
        item.setSectorId(itemDetails.getSectorId());
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id, String token) {
        String userId = validateTokenAndGetUserId(token);
        validatePolicy(userId, "delete", null);
        itemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsBySector(String sectorId, String token) {
        String userId = validateTokenAndGetUserId(token);
        validatePolicy(userId, "list", sectorId);
        return itemRepository.findBySectorId(sectorId);
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

    private void validatePolicy(String userId, String operation, String sectorId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Set<Policy> policies = user.getPolicies();
        boolean operationAllowed = false;
        boolean sectorAllowed = (sectorId == null); // If sectorId is null, skip sector check

        System.out.println("Validating policy for user ID: " + userId + " for operation: " + operation + " in sector: " + sectorId);

        for (Policy policy : policies) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode policyDefinition = mapper.readTree(policy.getDefinition());
                JsonNode allowedOperations = policyDefinition.get("allowedOperations");
                if (allowedOperations != null && allowedOperations.isArray()) {
                    for (JsonNode allowedOperation : allowedOperations) {
                        if (allowedOperation.asText().equals(operation)) {
                            operationAllowed = true;
                            break;
                        }
                    }
                }

                if (sectorId != null) {
                    JsonNode allowedSectors = policyDefinition.get("allowedSectors");
                    if (allowedSectors == null) {
                        // If no allowedSectors are defined, allow all sectors
                        sectorAllowed = true;
                        System.out.println("No allowed sectors defined, access to all sectors allowed.");
                    } else if (allowedSectors.isArray()) {
                        for (JsonNode allowedSector : allowedSectors) {
                            if (allowedSector.asText().equals(sectorId)) {
                                sectorAllowed = true;
                                break;
                            }
                        }
                    }
                }

                System.out.println("Operation allowed: " + operationAllowed + ", Sector allowed: " + sectorAllowed);

                if (operationAllowed && sectorAllowed) {
                    System.out.println("Operation and sector are allowed.");
                    return;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error parsing policy definition", e);
            }
        }

        if (!operationAllowed) {
            System.out.println("Operation not allowed.");
            throw new RuntimeException("User does not have permission to perform this operation");
        }

        if (!sectorAllowed) {
            System.out.println("Sector not allowed.");
            throw new RuntimeException("User does not have permission to access this sector");
        }
    }



    private Long generateItemId() {
        // Implement your logic to generate a unique ID
        return System.currentTimeMillis(); // Just an example
    }
}
