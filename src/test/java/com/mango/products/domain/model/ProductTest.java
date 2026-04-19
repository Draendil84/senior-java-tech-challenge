package com.mango.products.domain.model;

import com.mango.products.domain.exceptions.InvalidPriceException;
import com.mango.products.domain.exceptions.InvalidProductException;
import com.mango.products.domain.exceptions.PriceOverlapException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

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
        assertEquals("Zapatillas deportivas", product.getName());
        assertEquals("Modelo 2025 edición limitada", product.getDescription());
        assertTrue(product.getPrices().isEmpty());
    }

    @Test
    void shouldCreateProductWithNullId() {
        Product p = new Product("Nike", "Desc");

        assertNull(p.getId());
    }

    static Stream<String> invalidNamesProvider() {
        return Stream.of(
                null,
                "",
                "   "
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNamesProvider")
    void shouldThrowExceptionForInvalidNames(String name) {
        assertThrows(InvalidProductException.class,
                () -> new Product(name, "desc"));
    }

    @Test
    void shouldThrowExceptionWhenNameTooLong() {
        String longName = "a".repeat(256);

        assertThrows(InvalidProductException.class,
                () -> new Product(longName, "desc"));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionTooLong() {
        String longDesc = "a".repeat(1001);

        assertThrows(InvalidProductException.class,
                () -> new Product("name", longDesc));
    }

    @Test
    void shouldAddPriceSuccessfully() {
        product.addPrice(price(99.99, START_2024, END_2024));

        assertEquals(1, product.getPrices().size());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        assertThrows(InvalidPriceException.class,
                () -> product.addPrice(null));
    }

    @Test
    void shouldThrowExceptionWhenPricesOverlap() {
        product.addPrice(price(99.99, START_2024, END_2024));

        Price overlappingPrice = price(
                150.0,
                LocalDate.of(2024, 4, 1),
                LocalDate.of(2024, 8, 31)
        );

        assertThrows(PriceOverlapException.class,
                () -> product.addPrice(overlappingPrice));
    }

    @Test
    void shouldAllowMultipleNonOverlappingPrices() {
        product.addPrice(price(99.99, START_2024, END_2024));
        product.addPrice(price(199.99, START_2025, END_2025));

        assertEquals(2, product.getPrices().size());
    }

    static Stream<Object[]> priceSearchProvider() {
        return Stream.of(
                new Object[]{MID_2024, true, 99.99},
                new Object[]{START_2024, true, 99.99},
                new Object[]{END_2024, true, 99.99},
                new Object[]{START_2025, false, null}
        );
    }

    @ParameterizedTest
    @MethodSource("priceSearchProvider")
    void shouldEvaluatePriceSearch(LocalDate date, boolean expectedPresent, Double expectedValue) {
        product.addPrice(price(99.99, START_2024, END_2024));

        Optional<Price> result = product.getPriceAtDate(date);

        assertEquals(expectedPresent, result.isPresent());

        if (expectedPresent) {
            Price price = result.orElseThrow();
            assertEquals(BigDecimal.valueOf(expectedValue), price.getValue());
        }
    }

    @Test
    void shouldSupportOpenEndedPrice() {
        product.addPrice(price(99.99, START_2024, null));

        Optional<Price> result = product.getPriceAtDate(LocalDate.of(2030, 1, 1));

        assertTrue(result.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenNoPricesExist() {
        Optional<Price> result = product.getPriceAtDate(START_2024);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenNoMatchFound() {
        product.addPrice(price(99.99, START_2024, END_2024));

        Optional<Price> result = product.getPriceAtDate(START_2025);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenDateIsNull() {
        assertThrows(InvalidPriceException.class,
                () -> product.getPriceAtDate(null));
    }

    @Test
    void shouldReturnUnmodifiablePriceList() {
        product.addPrice(price(99.99, START_2024, END_2024));

        var prices = product.getPrices();
        Price newPrice = price(1.0, START_2025, END_2025);

        assertThrows(UnsupportedOperationException.class,
                () -> prices.add(newPrice));
    }

    private Price price(double value, LocalDate start, LocalDate end) {
        return new Price(BigDecimal.valueOf(value), start, end);
    }

}
