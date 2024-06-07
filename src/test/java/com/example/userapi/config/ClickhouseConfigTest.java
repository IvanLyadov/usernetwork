package com.example.userapi.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClickhouseConfigTest {

    @Test
    void testGettersAndSetters() {
        ClickhouseConfig config = new ClickhouseConfig();

        config.setUsername("testuser");
        config.setPassword("testpassword");

        assertThat(config.getUsername()).isEqualTo("testuser");
        assertThat(config.getPassword()).isEqualTo("testpassword");
    }

    @Test
    void testNoArgsConstructor() {
        ClickhouseConfig config = new ClickhouseConfig();

        assertThat(config.getUsername()).isNull();
        assertThat(config.getPassword()).isNull();
    }
}
