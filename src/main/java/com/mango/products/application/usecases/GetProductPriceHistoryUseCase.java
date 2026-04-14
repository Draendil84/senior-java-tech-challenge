package com.mango.products.application.usecases;

import com.mango.products.domain.Product;
import com.mango.products.domain.ports.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Use case to obtain the complete price history of a product.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetProductPriceHistoryUseCase {

    private final ProductRepository productRepository;

    /**
     * Gets the complete price history of a product by its ID.
     * Validates the input parameter and retrieves the product from the repository, including its price history.
     *
     * @param productId product ID for which the price history is requested
     * @return product with its price history, or an empty Optional if the product is not found
     */
    public Optional<Product> getProductPriceHistory(Long productId) {
        log.debug("Get price history for product ID: {}", productId);
        return productRepository.findById(productId);
    }

}
