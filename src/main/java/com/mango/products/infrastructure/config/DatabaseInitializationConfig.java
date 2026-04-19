package com.mango.products.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Configuration for initializing database schema.
 */
@Configuration
@EnableConfigurationProperties
public class DatabaseInitializationConfig {

    /**
     * Initializes the H2 database schema from schema.sql.
     * Only active when spring.sql.init.mode=always
     */
    @Bean
    @ConditionalOnProperty(
            name = "spring.sql.init.mode",
            havingValue = "always"
    )
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new org.springframework.core.io.ClassPathResource("schema.sql"));
        populator.setIgnoreFailedDrops(true);

        initializer.setDatabasePopulator(populator);
        initializer.setEnabled(true);

        return initializer;
    }

}
