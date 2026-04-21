package com.mango.products.application.usecases;

import com.mango.products.domain.exceptions.InvalidPriceException;
import com.mango.products.domain.exceptions.ProductNotFoundException;
import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.ports.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Use case for adding a price to a product.
 */
@Service
@RequiredArgsConstructor
public class AddPriceUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    private static final Logger log = LoggerFactory.getLogger(AddPriceUseCase.class);

    /**
     * Adds a new price to the specified product.
     *
     * @param productId The ID of the product to which the price will be added.
     * @param value     The price value to be added.
     * @param initDate  The start date of the price.
     * @param endDate   The end date of the price.
     * @return The updated product with the new price added.
     * @throws InvalidPriceException    if price violates domain rules (price <= 0, dates invalid, etc)
     * @throws ProductNotFoundException if product does not exist
     */
    public Product addPrice(Long productId, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        log.debug("Starting to add price to product ID: {}, value: {}, initDate: {}, endDate: {}",
                productId, value, initDate, endDate);

        Price price = new Price(value, initDate, endDate);

        Product product = productRepositoryPort.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.addPrice(price);

        Product savedProduct = productRepositoryPort.save(product);
        log.info("Product with ID: {} updated successfully with new price", productId);

        return savedProduct;
    }

}
