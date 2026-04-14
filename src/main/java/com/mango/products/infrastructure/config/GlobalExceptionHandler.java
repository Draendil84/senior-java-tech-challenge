package com.mango.products.infrastructure.config;

import com.mango.products.domain.exceptions.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the REST API.
 * Centralizes error handling and provides consistent responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Standard error response.
     */
    @Getter
    public static class ErrorResponse {
        public final String code;
        public final String message;
        public final int status;
        public final Map<String, Object> details;

        public ErrorResponse(String code, String message, int status) {
            this.code = code;
            this.message = message;
            this.status = status;
            this.details = new HashMap<>();
        }

    }

    /**
     * Handles argument validation errors
     *
     * @param ex MethodArgumentNotValidException thrown when @Valid fails
     * @return ErrorResponse with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                "Validation errors in the request",
                HttpStatus.BAD_REQUEST.value()
        );

        ex.getBindingResult().getFieldErrors().forEach(error ->
                response.details.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ProductNotFoundException (404).
     *
     * @param ex ProductNotFoundException thrown when a product is not found
     * @return ErrorResponse with error details and 404 status
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(
            ProductNotFoundException ex) {
        log.warn("Product not found: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DuplicateProductNameException (409 Conflict).
     *
     * @param ex DuplicateProductNameException thrown when trying to create a product with a name that already exists
     * @return ErrorResponse with error details and 409 status
     */
    @ExceptionHandler(DuplicateProductNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductNameException(
            DuplicateProductNameException ex) {
        log.warn("Duplicate product name: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles PriceOverlapException (409 Conflict).
     *
     * @param ex PriceOverlapException thrown when trying to add a price that overlaps with existing prices
     *           for the same product
     * @return ErrorResponse with error details and 409 status
     */
    @ExceptionHandler(PriceOverlapException.class)
    public ResponseEntity<ErrorResponse> handlePriceOverlapException(
            PriceOverlapException ex) {
        log.warn("Price overlap detected: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.CONFLICT.value());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles InvalidPriceException (400).
     *
     * @param ex InvalidPriceException thrown when trying to add a price with invalid data
     * @return ErrorResponse with error details and 400 status
     */
    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPriceException(
            InvalidPriceException ex) {
        log.warn("Invalid price: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles InvalidProductException (400).
     *
     * @param ex InvalidProductException thrown when trying to create or update a product with invalid data
     * @return ErrorResponse with error details and 400 status
     */
    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProductException(
            InvalidProductException ex) {
        log.warn("Invalid product: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic DomainException (400).
     *
     * @param ex DomainException thrown for any domain-related error that doesn't fit other specific exceptions
     * @return ErrorResponse with error details and 400 status
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex) {
        log.error("Domain error: {} - {}", ex.getCode(), ex.getMessage());
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic exceptions (500).
     *
     * @param ex Exception thrown for any unexpected error that is not handled by other specific exception handlers
     * @return ErrorResponse with error details and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {
        log.error("Unexpected error: ", ex);
        ErrorResponse response =
                new ErrorResponse("INTERNAL_ERROR", "Internal server error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
