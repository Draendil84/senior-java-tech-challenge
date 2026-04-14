package com.mango.products.domain.exceptions;

/**
 * Exception thrown when attempting to operate on a product that does not exist.
 */
public class ProductNotFoundException extends DomainException {

    /**
     * Constructor for ProductNotFoundException.
     *
     * @param productId product ID that was not found
     */
    public ProductNotFoundException(Long productId) {
        super("PRODUCT_NOT_FOUND", "Product not found with ID: " + productId);
    }
    
}
