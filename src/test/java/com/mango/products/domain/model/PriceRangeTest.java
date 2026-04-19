package com.mango.products.domain.model;

import com.mango.products.domain.exceptions.InvalidPriceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PriceRange.
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

    static Stream<Object[]> containsDateProvider() {
        return Stream.of(
                new Object[]{LocalDate.of(2024, 3, 1), START, END, true},
                new Object[]{START, START, END, true},
                new Object[]{END, START, END, true},
                new Object[]{LocalDate.of(2025, 1, 1), START, END, false},
                new Object[]{LocalDate.of(2030, 1, 1), START, null, true}
        );
    }

    @ParameterizedTest
    @MethodSource("containsDateProvider")
    void shouldEvaluateContainsDate(LocalDate date, LocalDate start, LocalDate end, boolean expected) {
        PriceRange range = new PriceRange(start, end);

        assertEquals(expected, range.containsDate(date));
    }

    @Test
    void shouldThrowExceptionWhenContainsDateIsNull() {
        PriceRange range = new PriceRange(START, END);

        assertThrows(InvalidPriceException.class,
                () -> range.containsDate(null));
    }

    static Stream<Object[]> overlapProvider() {
        return Stream.of(
                new Object[]{
                        new PriceRange(START, END),
                        new PriceRange(LocalDate.of(2024, 5, 1), LocalDate.of(2024, 12, 31)),
                        true
                },
                new Object[]{
                        new PriceRange(START, END),
                        new PriceRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 30)),
                        false
                },
                new Object[]{
                        new PriceRange(START, END),
                        null,
                        false
                },
                new Object[]{
                        new PriceRange(START, null),
                        new PriceRange(LocalDate.of(2023, 1, 1), null),
                        true
                },
                new Object[]{
                        new PriceRange(START, null),
                        new PriceRange(LocalDate.of(2024, 5, 1), LocalDate.of(2024, 12, 31)),
                        true
                },
                new Object[]{
                        new PriceRange(START, null),
                        new PriceRange(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)),
                        false
                },
                new Object[]{
                        new PriceRange(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)),
                        new PriceRange(START, null),
                        false
                },
                new Object[]{
                        new PriceRange(START, LocalDate.of(2024, 6, 30)),
                        new PriceRange(LocalDate.of(2024, 7, 1), LocalDate.of(2024, 12, 31)),
                        false
                },
                new Object[]{
                        new PriceRange(START, LocalDate.of(2024, 6, 30)),
                        new PriceRange(LocalDate.of(2024, 6, 30), LocalDate.of(2024, 12, 31)),
                        true
                }
        );
    }

    @ParameterizedTest
    @MethodSource("overlapProvider")
    void shouldEvaluateOverlaps(PriceRange r1, PriceRange r2, boolean expected) {
        assertEquals(expected, r1.overlapsWith(r2));

        if (r2 != null) {
            assertEquals(expected, r2.overlapsWith(r1));
        }
    }
}