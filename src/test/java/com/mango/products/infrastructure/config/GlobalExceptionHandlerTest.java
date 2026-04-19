package com.mango.products.infrastructure.config;

import com.mango.products.domain.exceptions.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleValidationExceptionWithErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getMessage()).thenReturn("Validation failed");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("object", "name", "must not be null"),
                new FieldError("object", "price", "must be positive")
        ));

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleValidationException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getCode());
        assertEquals(4, response.getBody().getDetails().size());
        assertEquals("must not be null", response.getBody().getDetails().get("name"));
        assertEquals("must be positive", response.getBody().getDetails().get("price"));
        assertTrue(response.getBody().getDetails().containsKey("reason"));
        assertTrue(response.getBody().getDetails().containsKey("action"));
    }

    @Test
    void shouldHandleValidationExceptionWithoutErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getMessage()).thenReturn("Validation failed");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleValidationException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        // Details should have at least reason and action, even without field errors
        assertTrue(response.getBody().getDetails().size() >= 2);
        assertTrue(response.getBody().getDetails().containsKey("reason"));
        assertTrue(response.getBody().getDetails().containsKey("action"));
    }

    @Test
    void shouldHandleProductNotFoundException() {
        ProductNotFoundException ex = new ProductNotFoundException(1L);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleProductNotFoundException(ex);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(ex.getCode(), response.getBody().getCode());
    }

    @Test
    void shouldHandleDuplicateProductNameException() {
        DuplicateProductNameException ex = new DuplicateProductNameException("Zapatillas");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleDuplicateProductNameException(ex);

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(ex.getCode(), response.getBody().getCode());
    }

    @Test
    void shouldHandlePriceOverlapException() {
        PriceOverlapException ex = new PriceOverlapException();

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handlePriceOverlapException(ex);

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(ex.getCode(), response.getBody().getCode());
    }

    @Test
    void shouldHandleInvalidPriceException() {
        InvalidPriceException ex = new InvalidPriceException("Precio inválido");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleInvalidPriceException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(ex.getCode(), response.getBody().getCode());
    }

    @Test
    void shouldHandleInvalidProductException() {
        InvalidProductException ex = new InvalidProductException("Producto inválido");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleInvalidProductException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(ex.getCode(), response.getBody().getCode());
    }

    @Test
    void shouldHandleDomainException() {
        DomainException ex = new DomainException("DOMAIN_ERROR", "Error de dominio");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleDomainException(ex);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("DOMAIN_ERROR", response.getBody().getCode());
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new RuntimeException("Unexpected");

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                handler.handleGenericException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().getCode());
    }

}
