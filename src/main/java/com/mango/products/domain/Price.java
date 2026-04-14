package com.mango.products.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Domain entity that represents a Price.
 */
@Getter
@Setter
@NoArgsConstructor
public class Price {
    private Long id;
    private BigDecimal value;
    private PriceRange range;

    public Price(Long id, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        this.id = id;
        this.value = value;
        this.range = new PriceRange(initDate, endDate);
    }

    public Price(BigDecimal value, LocalDate initDate, LocalDate endDate) {
        this(null, value, initDate, endDate);
    }

    /**
     * Getter to obtain the start date.
     */
    public LocalDate getInitDate() {
        return range != null ? range.getInitDate() : null;
    }

    /**
     * Getter to obtain the end date.
     */
    public LocalDate getEndDate() {
        return range != null ? range.getEndDate() : null;
    }

    /**
     * Setter to update the start date.
     *
     * @param initDate date to set as the start date.
     */
    public void setInitDate(LocalDate initDate) {
        if (range == null) {
            range = new PriceRange(initDate, null);
        } else {
            range = new PriceRange(initDate, range.getEndDate());
        }
    }

    /**
     * Setter to update the end date.
     *
     * @param endDate date to set as the end date.
     */
    public void setEndDate(LocalDate endDate) {
        if (range == null) {
            throw new IllegalStateException("It is not possible to set the end date without a start date");
        }
        range = new PriceRange(range.getInitDate(), endDate);
    }
    
}
