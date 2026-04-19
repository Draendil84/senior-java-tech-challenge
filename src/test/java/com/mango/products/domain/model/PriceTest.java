package com.mango.products.domain.model;

import com.mango.products.domain.exceptions.InvalidPriceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Price domain entity.
 */
class PriceTest {

    private static final BigDecimal VALID_VALUE = BigDecimal.valueOf(99.99);

    private static final LocalDate INIT = LocalDate.of(2024, 1, 1);
    private static final LocalDate END = LocalDate.of(2024, 6, 30);

    @Test
    void shouldCreatePriceSuccessfully() {
        Price price = new Price(VALID_VALUE, INIT, END);

        assertEquals(VALID_VALUE, price.getValue());
        assertEquals(INIT, price.getInitDate());
        assertEquals(END, price.getEndDate());
    }

    @Test
    void shouldCreatePriceWithIdSuccessfully() {
        Price price = new Price(1L, VALID_VALUE, INIT, END);

        assertEquals(1L, price.getId());
        assertEquals(VALID_VALUE, price.getValue());
    }

    static Stream<BigDecimal> invalidValuesProvider() {
        return Stream.of(
                null,
                BigDecimal.ZERO,
                new BigDecimal("-10"),
                new BigDecimal("0.00")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidValuesProvider")
    void shouldThrowExceptionForInvalidValues(BigDecimal value) {
        assertThrows(InvalidPriceException.class,
                () -> new Price(value, INIT, END));
    }

    static Stream<BigDecimal> validValuesProvider() {
        return Stream.of(
                new BigDecimal("0.01"),
                new BigDecimal("1.00"),
                new BigDecimal("99.99"),
                new BigDecimal("1000")
        );
    }

    @ParameterizedTest
    @MethodSource("validValuesProvider")
    void shouldAcceptValidValues(BigDecimal value) {
        Price price = new Price(value, INIT, END);

        assertEquals(value, price.getValue());
    }

    @Test
    void shouldHandleOpenEndedPrice() {
        Price price = new Price(VALID_VALUE, INIT, null);

        assertEquals(INIT, price.getInitDate());
        assertNull(price.getEndDate());
    }

    @Test
    void shouldCreateNewPriceWithUpdatedId() {
        Price original = new Price(VALID_VALUE, INIT, END);

        Price updated = original.withId(2L);

        assertNull(original.getId());
        assertEquals(2L, updated.getId());

        assertEquals(original.getValue(), updated.getValue());
        assertEquals(original.getInitDate(), updated.getInitDate());
        assertEquals(original.getEndDate(), updated.getEndDate());
    }

    @Test
    void shouldPreserveValueAndDatesAfterWithId() {
        Price price = new Price(VALID_VALUE, INIT, END);

        Price updated = price.withId(99L);

        assertEquals(VALID_VALUE, updated.getValue());
        assertEquals(INIT, updated.getInitDate());
        assertEquals(END, updated.getEndDate());
    }

}
