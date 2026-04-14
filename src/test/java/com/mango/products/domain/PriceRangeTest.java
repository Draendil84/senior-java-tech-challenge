package com.mango.products.domain;

import com.mango.products.domain.exceptions.InvalidPriceException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PriceRange.
 */
class PriceRangeTest {

    private static final LocalDate START = LocalDate.of(2024, 1, 1);
    private static final LocalDate END = LocalDate.of(2024, 6, 30);

    @Test
    void shouldCreatePriceRangeSuccessfully() {
        PriceRange range = new PriceRange(START, END);

        assertEquals(START, range.getInitDate());
        assertEquals(END, range.getEndDate());
    }

    @Test
    void shouldAllowOpenEndedRange() {
        PriceRange range = new PriceRange(START, null);

        assertEquals(START, range.getInitDate());
        assertNull(range.getEndDate());
    }

    @Test
    void shouldThrowExceptionWhenInitDateIsNull() {
        assertThrows(InvalidPriceException.class,
                () -> new PriceRange(null, END));
    }

    @Test
    void shouldThrowExceptionWhenInitAfterEnd() {
        assertThrows(InvalidPriceException.class,
                () -> new PriceRange(END, START));
    }

    @Test
    void shouldReturnTrueWhenDateIsInsideRange() {
        PriceRange range = new PriceRange(START, END);

        assertTrue(range.containsDate(LocalDate.of(2024, 3, 1)));
    }

    @Test
    void shouldReturnTrueWhenDateIsAtStartBoundary() {
        PriceRange range = new PriceRange(START, END);

        assertTrue(range.containsDate(START));
    }

    @Test
    void shouldReturnTrueWhenDateIsAtEndBoundary() {
        PriceRange range = new PriceRange(START, END);

        assertTrue(range.containsDate(END));
    }

    @Test
    void shouldReturnFalseWhenDateIsOutsideRange() {
        PriceRange range = new PriceRange(START, END);

        assertFalse(range.containsDate(LocalDate.of(2025, 1, 1)));
    }

    @Test
    void shouldHandleOpenEndedRangeInContainsDate() {
        PriceRange range = new PriceRange(START, null);

        assertTrue(range.containsDate(LocalDate.of(2030, 1, 1)));
    }

    @Test
    void shouldThrowExceptionWhenContainsDateIsNull() {
        PriceRange range = new PriceRange(START, END);

        assertThrows(InvalidPriceException.class,
                () -> range.containsDate(null));
    }

    @Test
    void shouldReturnTrueWhenRangesOverlap() {
        PriceRange r1 = new PriceRange(START, END);
        PriceRange r2 = new PriceRange(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 12, 31)
        );

        assertTrue(r1.overlapsWith(r2));
    }

    @Test
    void shouldReturnFalseWhenRangesDoNotOverlap() {
        PriceRange r1 = new PriceRange(START, END);
        PriceRange r2 = new PriceRange(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 6, 30)
        );

        assertFalse(r1.overlapsWith(r2));
    }

    @Test
    void shouldReturnFalseWhenOtherIsNull() {
        PriceRange range = new PriceRange(START, END);

        assertFalse(range.overlapsWith(null));
    }

    @Test
    void shouldDetectOverlapWhenOneIsOpenEnded() {
        PriceRange r1 = new PriceRange(START, null);
        PriceRange r2 = new PriceRange(
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 12, 31)
        );

        assertTrue(r1.overlapsWith(r2));
    }

    @Test
    void shouldDetectOverlapWhenBothAreOpenEnded() {
        PriceRange r1 = new PriceRange(START, null);
        PriceRange r2 = new PriceRange(
                LocalDate.of(2023, 1, 1),
                null
        );

        assertTrue(r1.overlapsWith(r2));
    }

    @Test
    void shouldNotOverlapWhenRangesTouchButDoNotIntersect() {
        PriceRange r1 = new PriceRange(
                START,
                LocalDate.of(2024, 6, 30)
        );

        PriceRange r2 = new PriceRange(
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 12, 31)
        );

        assertFalse(r1.overlapsWith(r2));
    }

}
