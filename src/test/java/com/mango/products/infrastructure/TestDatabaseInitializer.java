package com.mango.products.infrastructure;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * Test execution listener that initializes and cleans the database schema before each test.
 */
public class TestDatabaseInitializer implements TestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) {
        initializeSchema(testContext);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
        cleanAndReinitializeSchema(testContext);
    }

    private void initializeSchema(TestContext testContext) {
        JdbcTemplate jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);

        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS prices");
            jdbcTemplate.execute("DROP TABLE IF EXISTS products");

            createTables(jdbcTemplate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    private void cleanAndReinitializeSchema(TestContext testContext) {
        JdbcTemplate jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);

        try {
            jdbcTemplate.execute("DELETE FROM prices");
            jdbcTemplate.execute("DELETE FROM products");
        } catch (Exception e) {
            try {
                initializeSchema(testContext);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to reinitialize database schema", ex);
            }
        }
    }

    private void createTables(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("CREATE TABLE products (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(255) NOT NULL UNIQUE," +
                "description VARCHAR(1000)" +
                ")");

        jdbcTemplate.execute("CREATE TABLE prices (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "product_id BIGINT NOT NULL," +
                "price_value NUMERIC(19, 2) NOT NULL," +
                "init_date DATE NOT NULL," +
                "end_date DATE," +
                "FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE" +
                ")");

        try {
            jdbcTemplate.execute("CREATE INDEX idx_products_name ON products(name)");
            jdbcTemplate.execute("CREATE INDEX idx_prices_product_id ON prices(product_id)");
        } catch (Exception e) {
            // Indexes might fail if they already exist
        }
    }

}



