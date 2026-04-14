package com.mango.products.domain.exceptions;

/**
 * Exception thrown when attempting to create a product with a name that already exists.
 */
public class DuplicateProductNameException extends DomainException {

    /**
     * Constructor for DuplicateProductNameException.
     *
     * @param name name of the product that caused the exception
     */
    public DuplicateProductNameException(String name) {
        super("DUPLICATE_PRODUCT_NAME", "A product with that name already exists: " + name);
    }
    
}
