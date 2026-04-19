package com.mango.products.domain.model;

import com.mango.products.domain.exceptions.InvalidPriceException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Domain entity representing a Price.
 * <p>
 * This entity encapsulates business logic for price management including:
 * - Price value validation
 * - Temporal range management through PriceRange
 * - Price queries and temporal operations
 */
public class Price {
    private final Long id;
    private final BigDecimal value;
    private final PriceRange range;

    private static final BigDecimal MIN_PRICE = new BigDecimal("0.01");

    public Price(Long id, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        validateValue(value);
        this.id = id;
        this.value = value;
        this.range = new PriceRange(initDate, endDate);
    }

    public Price(BigDecimal value, LocalDate initDate, LocalDate endDate) {
        this(null, value, initDate, endDate);
    }

    /**
     * Validates price value according to domain rules.
     *
     * @param value price value to validate
     * @throws InvalidPriceException if value is null or less than minimum allowed
     */
    private static void validateValue(BigDecimal value) {
        if (value == null) {
            throw new InvalidPriceException("The price value cannot be null");
        }
        if (value.compareTo(MIN_PRICE) < 0) {
            throw new InvalidPriceException("The price value must be greater than 0");
        }
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public PriceRange getRange() {
        return range;
    }

    /**
     * Getter to obtain the start date.
     *
     * @return the start date of the price range
     */
    public LocalDate getInitDate() {
        return range != null ? range.getInitDate() : null;
    }

    /**
     * Getter to obtain the end date.
     *
     * @return the end date of the price range (null for open-ended ranges)
     */
    public LocalDate getEndDate() {
        return range != null ? range.getEndDate() : null;
    }
    
    /**
     * Creates a new Price instance with an updated ID.
     * This method is used during persistence operations.
     *
     * @param newId the new ID to assign
     * @return a new Price instance with the updated ID
     */
    public Price withId(Long newId) {
        return new Price(newId, this.value, this.getInitDate(), this.getEndDate());
    }

}
