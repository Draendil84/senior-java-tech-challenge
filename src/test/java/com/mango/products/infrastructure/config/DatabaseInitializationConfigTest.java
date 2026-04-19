package com.mango.products.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseInitializationConfigTest {

    private final ApplicationContextRunner runner =
            new ApplicationContextRunner()
                    .withUserConfiguration(DatabaseInitializationConfig.class);

    private DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Test
    void shouldCreateDataSourceInitializerWhenPropertyIsAlways() {
        runner
                .withBean(DataSource.class, this::dataSource)
                .withInitializer(context -> {
                    TestPropertyValues.of(
                            "spring.sql.init.mode=always"
                    ).applyTo(context);
                })
                .run(context -> {

                    assertTrue(context.containsBeanDefinition("dataSourceInitializer"));
                    assertTrue(context.containsBean(String.valueOf(DataSourceInitializer.class)));
                });
    }

    @Test
    void shouldNotCreateDataSourceInitializerWhenPropertyIsNotAlways() {
        runner
                .withBean(DataSource.class, this::dataSource)
                .withInitializer(context -> {
                    TestPropertyValues.of(
                            "spring.sql.init.mode=never"
                    ).applyTo(context);
                })
                .run(context -> {

                    assertFalse(context.containsBeanDefinition("dataSourceInitializer"));
                });
    }
}