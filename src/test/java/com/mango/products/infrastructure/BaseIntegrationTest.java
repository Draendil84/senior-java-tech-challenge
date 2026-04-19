package com.mango.products.infrastructure;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestExecutionListeners;

/**
 * Base class for integration tests that need database initialization.
 */
@SpringBootTest
@TestExecutionListeners(value = TestDatabaseInitializer.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class BaseIntegrationTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeSchema() {
        // Initialize schema for tests
        initializeTables();
    }

    protected void initializeTables() {
        // Create products table
        try {
            jdbcTemplate.execute("CREATE TABLE products (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(255) NOT NULL UNIQUE," +
                    "description VARCHAR(1000)" +
                    ")");
        } catch (Exception e) {
            // Table might already exist
        }

        // Create prices table
        try {
            jdbcTemplate.execute("CREATE TABLE prices (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                    "product_id BIGINT NOT NULL," +
                    "price_value NUMERIC(19, 2) NOT NULL," +
                    "init_date DATE NOT NULL," +
                    "end_date DATE," +
                    "FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE" +
                    ")");
        } catch (Exception e) {
            // Table might already exist
        }

        // Create indexes
        try {
            jdbcTemplate.execute("CREATE INDEX idx_products_name ON products(name)");
        } catch (Exception e) {
            // Index might already exist
        }

        try {
            jdbcTemplate.execute("CREATE INDEX idx_prices_product_id ON prices(product_id)");
        } catch (Exception e) {
            // Index might already exist
        }
    }

}
