package com.example.userapi.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataSourceConfigTest {

    @Test
    void testDataSource() {
        // Mock the VaultConfig
        VaultConfig vaultConfig = mock(VaultConfig.class);
        when(vaultConfig.getUser()).thenReturn("default");
        when(vaultConfig.getSecret()).thenReturn("cOBY8tbHMMK.E");

        // Create the DataSourceConfig instance
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        ReflectionTestUtils.setField(dataSourceConfig, "vaultConfig", vaultConfig);

        // Call the dataSource() method
        DataSource dataSource = dataSourceConfig.dataSource();

        // Verify the data source properties
        assertThat(dataSource).isInstanceOf(HikariDataSource.class);
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        assertThat(hikariDataSource.getJdbcUrl()).isEqualTo("jdbc:clickhouse://mu4s7vj2j6.us-east-1.aws.clickhouse.cloud:8443?ssl=true");
        assertThat(hikariDataSource.getUsername()).isEqualTo("default");
        assertThat(hikariDataSource.getPassword()).isEqualTo("cOBY8tbHMMK.E");
        assertThat(hikariDataSource.getDriverClassName()).isEqualTo("com.clickhouse.jdbc.ClickHouseDriver");
    }
}
