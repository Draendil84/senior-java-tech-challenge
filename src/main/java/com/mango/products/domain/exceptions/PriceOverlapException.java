package com.mango.products.domain.exceptions;

/**
 * Exception thrown when there is an overlap of dates in prices.
 */
public class PriceOverlapException extends DomainException {

    /**
     * Constructor for PriceOverlapException with a default message.
     */
    public PriceOverlapException() {
        super("PRICE_OVERLAP", "The date range overlaps with an existing price");
    }
    
}
