package com.mango.products.domain;

import com.mango.products.domain.exceptions.InvalidPriceException;
import com.mango.products.domain.exceptions.PriceOverlapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Product domain entity.
 */
class ProductTest {

    private Product product;

    private static final LocalDate START_2024 = LocalDate.of(2024, 1, 1);
    private static final LocalDate MID_2024 = LocalDate.of(2024, 4, 15);
    private static final LocalDate END_2024 = LocalDate.of(2024, 6, 30);
    private static final LocalDate START_2025 = LocalDate.of(2025, 1, 1);
    private static final LocalDate END_2025 = LocalDate.of(2025, 6, 30);

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Zapatillas deportivas", "Modelo 2025 edición limitada");
    }

    @Test
    void shouldCreateProductCorrectly() {
        assertAll(
                () -> assertEquals("Zapatillas deportivas", product.getName()),
                () -> assertEquals("Modelo 2025 edición limitada", product.getDescription()),
                () -> assertTrue(product.getPrices().isEmpty())
        );
    }

    @Test
    void shouldAddPriceSuccessfully() {
        product.addPrice(price(BigDecimal.valueOf(99.99), START_2024, END_2024));

        assertEquals(1, product.getPrices().size());
        assertEquals(BigDecimal.valueOf(99.99), product.getPrices().getFirst().getValue());
    }

    @Test
    void shouldAddMultiplePricesWhenNoOverlap() {
        product.addPrice(price(BigDecimal.valueOf(99.99), START_2024, END_2024));
        product.addPrice(price(BigDecimal.valueOf(199.99), START_2025, END_2025));

        assertEquals(2, product.getPrices().size());
    }

    @Test
    void shouldThrowExceptionWhenPricesOverlap() {
        product.addPrice(price(BigDecimal.valueOf(99.99), START_2024, END_2024));

        Price overlapping = price(
                BigDecimal.valueOf(150.00),
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 8, 31)
        );

        assertThrows(PriceOverlapException.class,
                () -> product.addPrice(overlapping));
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        assertThrows(InvalidPriceException.class,
                () -> product.addPrice(null));
    }

    @Test
    void shouldThrowExceptionWhenDateIsNull() {
        assertThrows(InvalidPriceException.class,
                () -> product.getPriceAtDate(null));
    }

    @Test
    void shouldThrowExceptionWhenDateRangeIsInvalid() {
        assertThrows(InvalidPriceException.class, this::createPriceWithInvalidRange);
    }

    @Test
    void shouldFindPriceAtGivenDate() {
        product.addPrice(price(BigDecimal.valueOf(99.99), START_2024, END_2024));

        Optional<Price> result = product.getPriceAtDate(MID_2024);

        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(99.99), result.get().getValue());
    }

    @Test
    void shouldReturnEmptyWhenPriceNotFound() {
        product.addPrice(price(BigDecimal.valueOf(99.99), START_2024, END_2024));

        Optional<Price> result = product.getPriceAtDate(START_2025);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldMatchBoundaryStartDate() {
        product.addPrice(price(BigDecimal.valueOf(99.99), START_2024, END_2024));

        assertTrue(product.getPriceAtDate(START_2024).isPresent());
    }

    @Test
    void shouldMatchBoundaryEndDate() {
        product.addPrice(price(BigDecimal.valueOf(99.99), START_2024, END_2024));

        assertTrue(product.getPriceAtDate(END_2024).isPresent());
    }

    @Test
    void shouldReturnFirstMatchingPriceWhenMultipleExist() {
        product.addPrice(price(BigDecimal.valueOf(99.99), START_2024, END_2024));
        product.addPrice(price(BigDecimal.valueOf(199.99), START_2025, END_2025));

        Optional<Price> result = product.getPriceAtDate(MID_2024);

        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(99.99), result.get().getValue());
    }

    @Test
    void shouldSupportOpenEndedPrice() {
        product.addPrice(price(BigDecimal.valueOf(99.99), START_2024, null));

        Optional<Price> result = product.getPriceAtDate(LocalDate.of(2030, 1, 1));

        assertTrue(result.isPresent());
    }

    private Price price(BigDecimal value, LocalDate start, LocalDate end) {
        return new Price(value, start, end);
    }

    private void createPriceWithInvalidRange() {
        new Price(
                BigDecimal.valueOf(99.99),
                LocalDate.of(2024, 6, 30),
                LocalDate.of(2024, 1, 1)
        );
    }

}
