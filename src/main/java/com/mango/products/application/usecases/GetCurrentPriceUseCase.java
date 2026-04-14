package com.mango.products.application.usecases;

import com.mango.products.domain.Price;
import com.mango.products.domain.exceptions.InvalidPriceException;
import com.mango.products.domain.ports.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Use case to obtain the current price of a product on a specific date.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetCurrentPriceUseCase {

    private final ProductRepository productRepository;

    /**
     * Gets the current price of a product for a specific date.
     * Validates the input parameters and retrieves the price from the product's price history.
     *
     * @param productId id of the product for which the price is requested
     * @param date      date for which the price is requested
     * @return price of the product for the specified date, or an empty Optional if no price is found for that date
     */
    public Optional<Price> getCurrentPrice(Long productId, LocalDate date) {
        log.debug("Get current price for product ID: {} on date: {}", productId, date);

        if (date == null) {
            log.error("Invalid date: date cannot be null");
            throw new InvalidPriceException("The date cannot be null");
        }

        return productRepository.findById(productId)
                .flatMap(product -> product.getPriceAtDate(date));
    }

}
