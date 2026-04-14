package com.mango.products.application.usecases;

import com.mango.products.domain.Product;
import com.mango.products.domain.exceptions.DuplicateProductNameException;
import com.mango.products.domain.exceptions.InvalidProductException;
import com.mango.products.domain.ports.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Use case for creating a new product.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    /**
     * Creates a new product with the given name and description.
     * Validates the input parameters and checks for duplicate product names before saving the product to the repository.
     *
     * @param name        name of the product
     * @param description description of the product
     * @return product created
     */
    public Product createProduct(String name, String description) {
        log.debug("Starting product creation with name: '{}', description: '{}'", name, description);

        if (name == null || name.isBlank()) {
            log.error("Invalid product name: name is null or blank");
            throw new InvalidProductException("The product name cannot be empty");
        }

        if (productRepository.findByName(name).isPresent()) {
            log.warn("Duplicate product name detected: '{}'", name);
            throw new DuplicateProductNameException(name);
        }

        log.info("Creating new product with name: '{}'", name);
        Product product = new Product(name, description);
        Product savedProduct = productRepository.save(product);
        log.info("Product saved successfully with ID: {}", savedProduct.getId());

        return savedProduct;
    }

}
