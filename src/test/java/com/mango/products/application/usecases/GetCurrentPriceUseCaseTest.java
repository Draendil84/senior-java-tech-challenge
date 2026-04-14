package com.mango.products.application.usecases;

import com.mango.products.domain.Price;
import com.mango.products.domain.Product;
import com.mango.products.domain.exceptions.InvalidPriceException;
import com.mango.products.domain.ports.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GetCurrentPriceUseCase.
 */
@ExtendWith(MockitoExtension.class)
class GetCurrentPriceUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetCurrentPriceUseCase getCurrentPriceUseCase;

    @Test
    void testGetCurrentPriceSuccessfully() {
        Product product = new Product(1L, "Zapatillas", "Descripción");
        Price price = new Price(
                BigDecimal.valueOf(99.99),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30)
        );
        product.addPrice(price);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Price> result = getCurrentPriceUseCase.getCurrentPrice(1L, LocalDate.of(2024, 4, 15));

        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(99.99), result.get().getValue());
    }

    @Test
    void testGetCurrentPriceNotFound() {
        Product product = new Product(1L, "Zapatillas", "Descripción");
        Price price = new Price(
                BigDecimal.valueOf(99.99),
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30)
        );
        product.addPrice(price);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Price> result = getCurrentPriceUseCase.getCurrentPrice(1L, LocalDate.of(2025, 4, 15));

        assertFalse(result.isPresent());
    }

    @Test
    void testGetCurrentPriceProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Price> result = getCurrentPriceUseCase.getCurrentPrice(999L, LocalDate.of(2024, 4, 15));

        assertFalse(result.isPresent());
    }

    @Test
    void testGetCurrentPriceWithNullDateThrowsException() {
        assertThrows(InvalidPriceException.class, () ->
                getCurrentPriceUseCase.getCurrentPrice(1L, null));
    }
    
}
