package com.mango.products.application.usecases;

import com.mango.products.domain.exceptions.DuplicateProductNameException;
import com.mango.products.domain.exceptions.InvalidProductException;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.ports.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * Use case for creating a new product.
 */
@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateProductUseCase.class);

    private final ProductRepositoryPort productRepositoryPort;

    /**
     * Creates a new product with the given name and description.
     * Validates that the product name is unique before saving to the repository.
     *
     * @param name        name of the product
     * @param description description of the product
     * @return product created
     * @throws DuplicateProductNameException if a product with this name already exists
     * @throws InvalidProductException       if domain invariants are violated (name validation, etc)
     */
    public Product createProduct(String name, String description) {
        log.debug("Starting product creation with name: '{}', description: '{}'", name, description);

        if (productRepositoryPort.findByName(name).isPresent()) {
            log.warn("Duplicate product name detected: '{}'", name);
            throw new DuplicateProductNameException(name);
        }

        log.info("Creating new product with name: '{}'", name);
        Product product = new Product(name, description);
        Product savedProduct = productRepositoryPort.save(product);
        log.info("Product saved successfully with ID: {}", savedProduct.getId());

        return savedProduct;
    }

}
