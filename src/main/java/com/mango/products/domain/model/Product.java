package com.mango.products.domain.model;

import com.mango.products.domain.exceptions.InvalidPriceException;
import com.mango.products.domain.exceptions.InvalidProductException;
import com.mango.products.domain.exceptions.PriceOverlapException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Domain entity representing a Product.
 */
public class Product {
    private final Long id;
    private final String name;
    private final String description;
    private final List<Price> prices;

    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;

    public Product(Long id, String name, String description) {
        validateName(name);
        validateDescription(description);
        this.id = id;
        this.name = name;
        this.description = description;
        this.prices = new ArrayList<>();
    }

    public Product(String name, String description) {
        this(null, name, description);
    }

    /**
     * Validates product name according to domain rules.
     *
     * @param name name to validate
     * @throws InvalidProductException if name is null, empty, or exceeds max length
     */
    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidProductException("The product name cannot be empty");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidProductException(
                    "The name must be between 1 and " + MAX_NAME_LENGTH + " characters long");
        }
    }

    /**
     * Validates product description according to domain rules.
     *
     * @param description description to validate
     * @throws InvalidProductException if description exceeds max length
     */
    private static void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new InvalidProductException(
                    "The description cannot exceed " + MAX_DESCRIPTION_LENGTH + " characters");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Price> getPrices() {
        return Collections.unmodifiableList(prices);
    }

    /**
     * Adds a new price to the product, validating that there is no date overlap.
     * Time range validation is delegated to PriceRange.
     *
     * @param newPrice price to be added to the product's price history
     * @throws InvalidPriceException if the price is null
     * @throws PriceOverlapException if the price range overlaps with existing prices
     */
    public void addPrice(Price newPrice) {
        if (newPrice == null) {
            throw new InvalidPriceException("The price cannot be null");
        }
        for (Price existingPrice : prices) {
            if (newPrice.getRange().overlapsWith(existingPrice.getRange())) {
                throw new PriceOverlapException();
            }
        }
        prices.add(newPrice);
    }

    /**
     * Gets the current price for a specific date.
     * Delegates date validation to PriceRange.
     *
     * @param date date for which the price is requested
     * @return Optional containing the price if found
     * @throws InvalidPriceException if the date is null
     */
    public Optional<Price> getPriceAtDate(LocalDate date) {
        if (date == null) {
            throw new InvalidPriceException("The date cannot be null");
        }
        return prices.stream()
                .filter(price -> price.getRange().containsDate(date))
                .findFirst();
    }

}
