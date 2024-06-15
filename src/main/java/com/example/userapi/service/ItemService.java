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

import java.util.HashSet;
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
    public List<Item> getAllItems(String userId, String token) {
        validateToken(userId, token);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Set<String> allowedSectors = getAllowedSectors(user);

        return itemRepository.findAll().stream()
                .filter(item -> allowedSectors.contains("ALL") || allowedSectors.contains(item.getSectorId()))
                .toList();
    }


    @Transactional(readOnly = true)
    public Optional<Item> getItemById(Long id, String userId, String token) {
        validateToken(userId, token);
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        validatePolicy(userId, "list", item.getSectorId());
        return Optional.of(item);
    }


    @Transactional
    public Item createItem(Item item, String userId, String token) {
        validateToken(userId, token);
        validatePolicy(userId, "create", item.getSectorId());
        item.setUserId(userId);
        item.setId(generateItemId());
        logger.info("Creating item: " + item.toString());
        Item savedItem = itemRepository.save(item);
        logger.info("Item created with ID: " + savedItem.getId());
        return savedItem;
    }

    @Transactional
    public Item updateItem(Long id, Item itemDetails, String userId, String token) {
        validateToken(userId, token);
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        validatePolicy(userId, "update", itemDetails.getSectorId());
        item.setDescription(itemDetails.getDescription());
        item.setSectorId(itemDetails.getSectorId());
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id, String userId, String token) {
        validateToken(userId, token);
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        validatePolicy(userId, "delete", item.getSectorId());
        itemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Item> getItemsBySector(String sectorId, String userId, String token) {
        validateToken(userId, token);
        validatePolicy(userId, "list", sectorId);
        return itemRepository.findBySectorId(sectorId);
    }

    private void validateToken(String userId, String token) {
        String vaultPath = "user-secrets/" + userId;
        VaultResponse response = kvOperations.get(vaultPath);
        if (response == null || response.getData() == null) {
            throw new RuntimeException("Invalid token");
        }
        String storedToken = (String) response.getData().get("secret");
        if (!token.equals(storedToken)) {
            throw new RuntimeException("Invalid token");
        }
    }

    private void validatePolicy(String userId, String operation, String sectorId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Set<Policy> policies = user.getPolicies();
        boolean operationAllowed = false;
        boolean sectorAllowed = false;

        for (Policy policy : policies) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode policyDefinition = mapper.readTree(policy.getDefinition());
                JsonNode allowedOperations = policyDefinition.get("allowedOperations");
                JsonNode allowedSectors = policyDefinition.get("allowedSectors");

                // Check if operation is allowed
                if (allowedOperations != null && allowedOperations.isArray()) {
                    for (JsonNode allowedOperation : allowedOperations) {
                        if (allowedOperation.asText().equals(operation)) {
                            operationAllowed = true;
                            break;
                        }
                    }
                }

                // Check if sector is allowed
                if (allowedSectors == null || allowedSectors.isArray() && allowedSectors.size() == 0) {
                    // If allowedSectors is not specified or empty, allow access to all sectors
                    sectorAllowed = true;
                } else if (allowedSectors != null && allowedSectors.isArray()) {
                    for (JsonNode allowedSector : allowedSectors) {
                        if (allowedSector.asText().equals(sectorId)) {
                            sectorAllowed = true;
                            break;
                        }
                    }
                }

                // If both operation and sector are allowed, return
                if (operationAllowed && sectorAllowed) {
                    return;
                }
            } catch (Exception e) {
                throw new RuntimeException("Error parsing policy definition", e);
            }
        }

        if (!operationAllowed) {
            throw new RuntimeException("User does not have permission to perform this operation");
        }

        if (!sectorAllowed) {
            throw new RuntimeException("User does not have permission to access this sector");
        }
    }

    private Set<String> getAllowedSectors(User user) {
        Set<String> allowedSectors = new HashSet<>();
        boolean hasAllowedSectors = false;

        for (Policy policy : user.getPolicies()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode policyDefinition = mapper.readTree(policy.getDefinition());
                JsonNode sectors = policyDefinition.get("allowedSectors");
                if (sectors != null && sectors.isArray()) {
                    hasAllowedSectors = true;
                    for (JsonNode sector : sectors) {
                        allowedSectors.add(sector.asText());
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error parsing policy definition", e);
            }
        }

        // If no allowedSectors are defined in any policy, allow access to all sectors
        if (!hasAllowedSectors) {
            allowedSectors.add("ALL");
        }

        return allowedSectors;
    }

    private Long generateItemId() {
        // Implement your logic to generate a unique ID
        return System.currentTimeMillis(); // Just an example
    }
}

