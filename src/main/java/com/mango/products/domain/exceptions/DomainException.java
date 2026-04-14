package com.mango.products.domain.exceptions;

import lombok.Getter;

/**
 * Base exception for domain errors.
 * Used in the domain layer to propagate business logic errors.
 */
@Getter
public class DomainException extends RuntimeException {

    private final String code;

    /**
     * Constructor for DomainException.
     *
     * @param code    code representing the type of error
     * @param message message describing the error
     */
    public DomainException(String code, String message) {
        super(message);
        this.code = code;
    }

}
