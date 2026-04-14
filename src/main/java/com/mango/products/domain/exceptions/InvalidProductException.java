package com.mango.products.domain.exceptions;

/**
 * Exception thrown when there are validation errors in the products.
 */
public class InvalidProductException extends DomainException {

    /**
     * Constructor for InvalidProductException.
     *
     * @param message message describing the validation error
     */
    public InvalidProductException(String message) {
        super("INVALID_PRODUCT", message);
    }

}
