package com.mango.products.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Price domain entity.
 */
class PriceTest {

    private static final BigDecimal VALUE = BigDecimal.valueOf(99.99);
    private static final LocalDate INIT = LocalDate.of(2024, 1, 1);
    private static final LocalDate END = LocalDate.of(2024, 6, 30);

    @Test
    void shouldCreatePriceSuccessfully() {
        Price price = new Price(VALUE, INIT, END);

        assertNotNull(price);
        assertEquals(VALUE, price.getValue());
        assertEquals(INIT, price.getInitDate());
        assertEquals(END, price.getEndDate());
    }

    @Test
    void shouldCreatePriceWithIdSuccessfully() {
        Price price = new Price(1L, VALUE, INIT, END);

        assertEquals(1L, price.getId());
        assertEquals(VALUE, price.getValue());
    }

    @Test
    void shouldReturnNullDatesWhenRangeIsNull() {
        Price price = new Price();
        price.setValue(VALUE);

        assertNull(price.getInitDate());
        assertNull(price.getEndDate());
    }

    @Test
    void shouldUpdateInitDateWhenRangeExists() {
        Price price = new Price(VALUE, INIT, END);

        LocalDate newInit = LocalDate.of(2023, 1, 1);
        price.setInitDate(newInit);

        assertEquals(newInit, price.getInitDate());
        assertEquals(END, price.getEndDate());
    }

    @Test
    void shouldCreateRangeWhenSettingInitDateOnNullRange() {
        Price price = new Price();
        price.setValue(VALUE);

        LocalDate newInit = LocalDate.of(2023, 1, 1);
        price.setInitDate(newInit);

        assertEquals(newInit, price.getInitDate());
        assertNull(price.getEndDate());
    }

    @Test
    void shouldUpdateEndDateSuccessfully() {
        Price price = new Price(VALUE, INIT, END);

        LocalDate newEnd = LocalDate.of(2024, 12, 31);
        price.setEndDate(newEnd);

        assertEquals(INIT, price.getInitDate());
        assertEquals(newEnd, price.getEndDate());
    }

    @Test
    void shouldThrowExceptionWhenSettingEndDateWithoutInitDate() {
        Price price = new Price();

        assertThrows(IllegalStateException.class, () -> price.setEndDate(END));
    }

    @Test
    void shouldHandleOpenEndedPrice() {
        Price price = new Price(VALUE, INIT, null);

        assertEquals(INIT, price.getInitDate());
        assertNull(price.getEndDate());
    }

    @Test
    void shouldPreserveValueWhenUpdatingDates() {
        Price price = new Price(VALUE, INIT, END);

        price.setInitDate(LocalDate.of(2023, 5, 1));
        price.setEndDate(LocalDate.of(2025, 5, 1));

        assertEquals(VALUE, price.getValue());
    }

}
