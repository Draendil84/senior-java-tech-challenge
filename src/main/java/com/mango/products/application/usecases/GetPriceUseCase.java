package com.mango.products.application.usecases;

import com.mango.products.domain.model.Price;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.ports.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Use case to get product price information.
 */
@Service
@RequiredArgsConstructor
public class GetPriceUseCase {

    private final ProductRepository productRepository;

    private static final Logger log = LoggerFactory.getLogger(GetPriceUseCase.class);

    /**
     * Gets price information for a product.
     * If a specific date is provided, returns the price at that date.
     * Otherwise, returns the complete price history.
     *
     * @param productId product ID
     * @param date      optional date to get specific price. If null, returns price history
     * @return PriceInfo object containing either a specific price or price history
     */
    public PriceInfo getPrice(Long productId, LocalDate date) {
        if (date != null) {
            log.debug("Get current price for product ID: {} on date: {}", productId, date);
            return getCurrentPriceInfo(productId, date);
        } else {
            log.debug("Get price history for product ID: {}", productId);
            return getPriceHistoryInfo(productId);
        }
    }

    /**
     * Gets the current price of a product for a specific date.
     *
     * @param productId product ID
     * @param date      date for which the price is requested
     * @return PriceInfo with current price or empty if not found
     */
    private PriceInfo getCurrentPriceInfo(Long productId, LocalDate date) {
        Optional<Price> price = productRepository.findById(productId)
                .flatMap(product -> product.getPriceAtDate(date));

        return new PriceInfo(price, Optional.empty());
    }

    /**
     * Gets the complete price history of a product by its ID.
     *
     * @param productId product ID for which the price history is requested
     * @return PriceInfo with product and its price history or empty if not found
     */
    private PriceInfo getPriceHistoryInfo(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return new PriceInfo(Optional.empty(), product);
    }

    /**
     * Value object representing price information.
     * Contains either a specific price or a product with price history.
     */
    public record PriceInfo(
            Optional<Price> currentPrice,
            Optional<Product> productWithHistory
    ) {
        /**
         * Checks if this PriceInfo contains price data (either current price or history).
         */
        public boolean hasData() {
            return currentPrice.isPresent() || productWithHistory.isPresent();
        }

        /**
         * Gets the current price if available.
         */
        public Optional<Price> getCurrentPrice() {
            return currentPrice;
        }

        /**
         * Gets the product with price history if available.
         */
        public Optional<Product> getProductWithHistory() {
            return productWithHistory;
        }
    }
}

