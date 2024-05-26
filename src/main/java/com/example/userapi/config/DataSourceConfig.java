package com.example.userapi.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Autowired
    private VaultConfig vaultConfig;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:clickhouse://mu4s7vj2j6.us-east-1.aws.clickhouse.cloud:8443?ssl=true");
        dataSource.setUsername(vaultConfig.getUsername());
        dataSource.setPassword(vaultConfig.getPassword());
        dataSource.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");
        return dataSource;
    }
}
