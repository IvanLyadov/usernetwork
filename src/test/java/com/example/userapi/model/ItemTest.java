package com.example.userapi.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Test
    void testGettersAndSetters() {
        Item item = new Item();

        item.setId(1L);
        item.setUserId("user123");
        item.setSectorId("sector456");
        item.setName("Item Name");
        item.setDescription("Item Description");

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getUserId()).isEqualTo("user123");
        assertThat(item.getSectorId()).isEqualTo("sector456");
        assertThat(item.getName()).isEqualTo("Item Name");
        assertThat(item.getDescription()).isEqualTo("Item Description");
    }

    @Test
    void testToString() {
        Item item = new Item();
        item.setId(1L);
        item.setUserId("user123");
        item.setSectorId("sector456");
        item.setName("Item Name");
        item.setDescription("Item Description");

        String expected = "Item{id=1, userId='user123', sectorId='sector456', name='Item Name', description='Item Description'}";
        assertThat(item.toString()).isEqualTo(expected);
    }

    @Test
    void testNoArgsConstructor() {
        Item item = new Item();

        assertThat(item.getId()).isNull();
        assertThat(item.getUserId()).isNull();
        assertThat(item.getSectorId()).isNull();
        assertThat(item.getName()).isNull();
        assertThat(item.getDescription()).isNull();
    }
}
