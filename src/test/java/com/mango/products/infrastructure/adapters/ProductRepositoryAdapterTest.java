package com.mango.products.infrastructure.adapters;

import com.mango.products.domain.Price;
import com.mango.products.domain.Product;
import com.mango.products.domain.exceptions.DuplicateProductNameException;
import com.mango.products.infrastructure.persistence.product.ProductJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductRepositoryAdapter.
 */
@DataJpaTest
@ComponentScan(basePackages = {
        "com.mango.products.infrastructure.adapters",
        "com.mango.products.infrastructure.persistence.product"
})
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

    @Autowired
    private ProductJpaRepository jpaRepository;

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
    void shouldUpdateProduct() {
        Product saved = repository.save(newProduct());

        saved.setName("Zapatillas Premium");

        Product updated = repository.save(saved);

        assertEquals("Zapatillas Premium", updated.getName());
    }

    @Test
    void shouldPersistProductWithMultiplePrices() {
        Product product = newProduct();
        product.addPrice(newPrice(PRICE_1, START_DATE_1, END_DATE_1));
        product.addPrice(newPrice(PRICE_2, START_DATE_2, END_DATE_2));

        Product saved = repository.save(product);

        jpaRepository.flush();

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
