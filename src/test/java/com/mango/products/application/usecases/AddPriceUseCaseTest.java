package com.mango.products.application.usecases;

import com.mango.products.domain.exceptions.InvalidPriceException;
import com.mango.products.domain.exceptions.ProductNotFoundException;
import com.mango.products.domain.model.Product;
import com.mango.products.domain.ports.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AddPriceUseCase.
 */
@ExtendWith(MockitoExtension.class)
class AddPriceUseCaseTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private AddPriceUseCase addPriceUseCase;

    @Test
    void testAddPriceSuccessfully() {
        Product product = new Product(1L, "Zapatillas", "Descripción");
        when(productRepositoryPort.findById(1L)).thenReturn(Optional.of(product));
        when(productRepositoryPort.save(any(Product.class))).thenReturn(product);

        Product result = addPriceUseCase.addPrice(
                1L,
                BigDecimal.valueOf(99.99),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30)
        );

        assertNotNull(result);
        assertEquals(1, result.getPrices().size());
    }

    @Test
    void testAddPriceProductNotFoundThrowsException() {
        Long productId = 999L;
        BigDecimal price = BigDecimal.valueOf(99.99);
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 6, 30);

        when(productRepositoryPort.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> addPriceUseCase.addPrice(productId, price, start, end));
    }

    @Test
    void testAddPriceWithNullValueThrowsException() {
        Long productId = 1L;
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 6, 30);

        assertThrows(InvalidPriceException.class,
                () -> addPriceUseCase.addPrice(productId, null, start, end));
    }

    @Test
    void testAddPriceWithZeroValueThrowsException() {
        Long productId = 1L;
        BigDecimal price = BigDecimal.ZERO;
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 6, 30);

        assertThrows(InvalidPriceException.class,
                () -> addPriceUseCase.addPrice(productId, price, start, end));
    }

    @Test
    void testAddPriceWithNullInitDateThrowsException() {
        Long productId = 1L;
        BigDecimal price = BigDecimal.valueOf(99.99);
        LocalDate end = LocalDate.of(2024, 6, 30);

        // Domain validates date range BEFORE use case even checks if product exists
        assertThrows(InvalidPriceException.class,
                () -> addPriceUseCase.addPrice(productId, price, null, end));
    }

}
