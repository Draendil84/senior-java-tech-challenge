package com.mango.products.infrastructure.persistence.product;

import com.mango.products.domain.exceptions.DuplicateProductNameException;
import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.infrastructure.TestDatabaseInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ProductRepositoryAdapter.
 */
@SpringBootTest
@Transactional
@TestExecutionListeners(value = TestDatabaseInitializer.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
class ProductRepositoryAdapterTest {

    private static final String NAME = "Zapatillas";
    private static final String DESCRIPTION = "Descripción";

    private static final BigDecimal PRICE_1 = BigDecimal.valueOf(99.99);
    private static final BigDecimal PRICE_2 = BigDecimal.valueOf(199.99);

    private static final LocalDate START_DATE_1 = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE_1 = LocalDate.of(2024, 6, 30);

    private static final LocalDate START_DATE_2 = LocalDate.of(2025, 1, 1);
    private static final LocalDate END_DATE_2 = LocalDate.of(2025, 6, 30);

    @Autowired
    private ProductRepositoryAdapter repository;

    @Test
    void shouldSaveProduct() {
        Product product = newProduct();

        Product saved = repository.save(product);

        assertNotNull(saved.getId());
        assertEquals(NAME, saved.getName());
    }

    @Test
    void shouldSaveProductWithPrice() {
        Product product = newProduct();
        product.addPrice(newPrice(PRICE_1, START_DATE_1, END_DATE_1));

        Product saved = repository.save(product);

        assertProductHasPrices(saved, 1);
        assertEquals(PRICE_1, saved.getPrices().getFirst().getValue());
    }

    @Test
    void shouldFindProductById() {
        Product saved = repository.save(newProduct());

        Product found = repository.findById(saved.getId()).orElseThrow();

        assertEquals(NAME, found.getName());
    }

    @Test
    void shouldFindProductByName() {
        repository.save(newProduct());

        Product found = repository.findByName(NAME).orElseThrow();

        assertEquals(NAME, found.getName());
    }

    @Test
    void shouldReturnEmptyWhenProductNotFound() {
        assertTrue(repository.findById(999L).isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenFindByNameNotFound() {
        assertTrue(repository.findByName("NoExiste").isEmpty());
    }

    @Test
    void shouldReturnAllProducts() {
        repository.save(newProduct());

        List<Product> products = repository.findAll();

        assertEquals(1, products.size());
    }

    @Test
    void shouldDeleteProductById() {
        Product saved = repository.save(newProduct());

        repository.deleteById(saved.getId());

        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    @Test
    void shouldUpdateProductWithNewName() {
        Product saved = repository.save(newProduct());

        // Create a new product with the saved ID but updated name
        Product updated = new Product(saved.getId(), "Zapatillas Premium", DESCRIPTION);

        Product result = repository.save(updated);

        assertEquals("Zapatillas Premium", result.getName());
    }

    @Test
    void shouldPersistProductWithMultiplePrices() {
        Product product = newProduct();
        product.addPrice(newPrice(PRICE_1, START_DATE_1, END_DATE_1));
        product.addPrice(newPrice(PRICE_2, START_DATE_2, END_DATE_2));

        Product saved = repository.save(product);

        Product retrieved = repository.findById(saved.getId()).orElseThrow();

        assertProductHasPrices(retrieved, 2);
    }

    @Test
    void shouldThrowDuplicateProductNameException() {
        repository.save(newProduct());

        Product duplicate = newProduct();

        assertThrows(DuplicateProductNameException.class,
                () -> repository.save(duplicate));
    }

    private Product newProduct() {
        return new Product(NAME, DESCRIPTION);
    }

    private Price newPrice(BigDecimal value, LocalDate start, LocalDate end) {
        return new Price(value, start, end);
    }

    private void assertProductHasPrices(Product product, int expectedSize) {
        assertEquals(expectedSize, product.getPrices().size());
    }

}
