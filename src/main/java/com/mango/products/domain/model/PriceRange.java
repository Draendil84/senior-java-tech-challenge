package com.mango.products.domain.model;

import com.mango.products.domain.exceptions.InvalidPriceException;

import java.time.LocalDate;

/**
 * Value object that represents a time range for a price.
 */
public class PriceRange {
    private final LocalDate initDate;
    private final LocalDate endDate;

    /**
     * Constructor that validates the date range.
     *
     * @param initDate start date (required)
     * @param endDate  end date (can be null to indicate open-ended)
     * @throws InvalidPriceException if initDate is null or if initDate > endDate
     */
    public PriceRange(LocalDate initDate, LocalDate endDate) {
        if (initDate == null) {
            throw new InvalidPriceException("The start date cannot be null");
        }

        if (endDate != null && initDate.isAfter(endDate)) {
            throw new InvalidPriceException("The start date must be earlier than or equal to the end date");
        }

        this.initDate = initDate;
        this.endDate = endDate;
    }

    public LocalDate getInitDate() {
        return initDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Checks if this range overlaps with another given range.
     *
     * @param other another PriceRange to compare
     * @return true if the ranges overlap, false otherwise
     */
    public boolean overlapsWith(PriceRange other) {
        if (other == null) {
            return false;
        }
        LocalDate thisEnd = this.endDate;
        LocalDate otherEnd = other.endDate;

        if (thisEnd == null && otherEnd == null) {
            return true;
        }

        if (thisEnd == null) {
            return !this.initDate.isAfter(otherEnd);
        }

        if (otherEnd == null) {
            return !other.initDate.isAfter(thisEnd);
        }

        return !(thisEnd.isBefore(other.initDate) || otherEnd.isBefore(this.initDate));
    }

    /**
     * Checks if a date is within this price range.
     *
     * @param date date to check
     * @return true if the date is within the range, false otherwise
     * @throws InvalidPriceException if the date is null
     */
    public boolean containsDate(LocalDate date) {
        if (date == null) {
            throw new InvalidPriceException("The date cannot be null");
        }
        boolean afterOrEqualStart = !date.isBefore(this.initDate);
        boolean beforeOrEqualEnd = this.endDate == null || !date.isAfter(this.endDate);
        return afterOrEqualStart && beforeOrEqualEnd;
    }

}
