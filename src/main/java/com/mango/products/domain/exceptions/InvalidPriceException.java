package com.mango.products.domain.exceptions;

/**
 * Exception to indicate that a price is invalid, either because it is null or because it has an incorrect format.
 */
public class InvalidPriceException extends DomainException {

    /**
     * Constructor for InvalidPriceException.
     *
     * @param message message describing the reason for the invalid price
     */
    public InvalidPriceException(String message) {
        super("INVALID_PRICE", message);
    }

}
