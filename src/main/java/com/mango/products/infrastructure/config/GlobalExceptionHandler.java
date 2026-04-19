package com.mango.products.infrastructure.config;

import com.mango.products.domain.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler for the REST API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String REASON = "reason";
    private static final String ACTION = "action";

    /**
     * Standard error response.
     */
    public static class ErrorResponse {
        public final String errorId;
        public final String code;
        public final String message;
        public final int status;
        public final String timestamp;
        public final Map<String, Object> details;

        public ErrorResponse(String code, String message, int status) {
            this.errorId = UUID.randomUUID().toString();
            this.code = code;
            this.message = message;
            this.status = status;
            this.timestamp = Instant.now().toString();
            this.details = new HashMap<>();
        }

        public String getCode() {
            return code;
        }

        public Map<String, Object> getDetails() {
            return details;
        }
    }

    /**
     * Handles argument validation errors (400 Bad Request).
     *
     * @param ex MethodArgumentNotValidException thrown when @Valid fails
     * @return ErrorResponse with validation error details and errorId for tracing
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {
        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                "Validation errors in the request",
                HttpStatus.BAD_REQUEST.value()
        );

        ex.getBindingResult().getFieldErrors().forEach(error ->
                response.details.put(error.getField(), error.getDefaultMessage())
        );

        response.details.put(REASON, "One or more request fields failed validation");
        response.details.put(ACTION, "Review the validation errors and resend the request with valid data");

        log.warn("[{}] Validation error: {}", response.errorId, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ProductNotFoundException (404 Not Found).
     *
     * @param ex ProductNotFoundException thrown when a product is not found
     * @return ErrorResponse with error details, errorId and 404 status
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(
            ProductNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.NOT_FOUND.value());
        response.details.put(REASON, "The requested product does not exist");
        response.details.put(ACTION, "Verify the product ID and try again, or create the product first");

        log.warn("[{}] Product not found: {}", response.errorId, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DuplicateProductNameException (409 Conflict).
     *
     * @param ex DuplicateProductNameException thrown when trying to create a product with a duplicate name
     * @return ErrorResponse with error details, errorId and 409 status
     */
    @ExceptionHandler(DuplicateProductNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductNameException(
            DuplicateProductNameException ex) {
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.CONFLICT.value());
        response.details.put(REASON, "A product with this name already exists in the database");
        response.details.put(ACTION, "Use a different product name or update the existing product");

        log.warn("[{}] Duplicate product name: {}", response.errorId, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles PriceOverlapException (409 Conflict).
     *
     * @param ex PriceOverlapException thrown when trying to add a price with overlapping date ranges
     * @return ErrorResponse with error details, errorId and 409 status
     */
    @ExceptionHandler(PriceOverlapException.class)
    public ResponseEntity<ErrorResponse> handlePriceOverlapException(
            PriceOverlapException ex) {
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.CONFLICT.value());
        response.details.put(REASON, "The price date range overlaps with an existing price for this product");
        response.details.put(ACTION, "Use a different date range that does not overlap with existing prices");

        log.warn("[{}] Price overlap detected: {}", response.errorId, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles InvalidPriceException (400 Bad Request).
     *
     * @param ex InvalidPriceException thrown when trying to add a price with invalid data
     * @return ErrorResponse with error details, errorId and 400 status
     */
    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPriceException(
            InvalidPriceException ex) {
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        response.details.put(REASON, "Price validation failed - check value and date range");
        response.details.put(ACTION, "Ensure price value > 0.00 and initDate <= endDate");

        log.warn("[{}] Invalid price: {}", response.errorId, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles InvalidProductException (400 Bad Request).
     *
     * @param ex InvalidProductException thrown when trying to create/update a product with invalid data
     * @return ErrorResponse with error details, errorId and 400 status
     */
    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProductException(
            InvalidProductException ex) {
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        response.details.put(REASON, "Product validation failed - check name and description");
        response.details.put(ACTION, "Ensure name is not empty and within length limits");

        log.warn("[{}] Invalid product: {}", response.errorId, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic DomainException (400 Bad Request).
     *
     * @param ex DomainException thrown for any domain-related error
     * @return ErrorResponse with error details, errorId and 400 status
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(
            DomainException ex) {
        ErrorResponse response = new ErrorResponse(ex.getCode(), ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        response.details.put(REASON, "Business rule violation");
        response.details.put(ACTION, "Review the error message and verify your request complies with business rules");

        log.error("[{}] Domain error - {} : {}", response.errorId, ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic exceptions (500 Internal Server Error).
     *
     * @param ex Exception thrown for any unexpected error
     * @return ErrorResponse with error details, errorId and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {
        ErrorResponse response =
                new ErrorResponse("INTERNAL_ERROR", "Internal server error",
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.details.put(REASON, "An unexpected error occurred");
        response.details.put(ACTION, "Contact support with the errorId for further assistance");

        log.error("[{}] Unexpected error: ", response.errorId, ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
