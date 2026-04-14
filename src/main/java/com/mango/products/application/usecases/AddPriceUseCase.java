package com.mango.products.application.usecases;

import com.mango.products.domain.Price;
import com.mango.products.domain.Product;
import com.mango.products.domain.exceptions.InvalidPriceException;
import com.mango.products.domain.exceptions.ProductNotFoundException;
import com.mango.products.domain.ports.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Use case for adding a price to a product.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddPriceUseCase {

    private final ProductRepository productRepository;

    /**
     * Adds a new price to the specified product. Validates the input parameters and updates the product's price history.
     *
     * @param productId The ID of the product to which the price will be added.
     * @param value     The price value to be added.
     * @param initDate  The start date of the price.
     * @param endDate   The end date of the price.
     * @return The updated product with the new price added.
     */
    public Product addPrice(Long productId, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        log.debug("Starting to add price to product ID: {}, value: {}, initDate: {}, endDate: {}",
                productId, value, initDate, endDate);

        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid price value: {}", value);
            throw new InvalidPriceException("The price value must be greater than 0");
        }

        if (initDate == null) {
            log.error("Invalid start date: null");
            throw new InvalidPriceException("The start date cannot be null");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Price price = new Price(value, initDate, endDate);
        product.addPrice(price);
        
        Product savedProduct = productRepository.save(product);
        log.info("Product with ID: {} updated successfully with new price", productId);

        return savedProduct;
    }

}
