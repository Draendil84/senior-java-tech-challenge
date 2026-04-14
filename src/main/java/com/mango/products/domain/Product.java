package com.mango.products.domain;

import com.mango.products.domain.exceptions.InvalidPriceException;
import com.mango.products.domain.exceptions.PriceOverlapException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Domain entity that represents a Product.
 */
@Getter
@Setter
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String description;
    private List<Price> prices;

    public Product(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.prices = new ArrayList<>();
    }

    public Product(String name, String description) {
        this(null, name, description);
    }

    /**
     * Adds a new price to the product, validating that there is no date overlap.
     * Time range validation is delegated to PriceRange.
     *
     * @param newPrice price to be added to the product's price history
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
     * @return price of the product for the specified date
     */
    public Optional<Price> getPriceAtDate(LocalDate date) {
        if (date == null) {
            throw new InvalidPriceException("The date cannot be null");
        }
        return prices.stream().filter(price -> price.getRange().containsDate(date)).findFirst();
    }

}
